package com.eventorback.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.auth.domain.RefreshToken;
import com.eventorback.auth.repository.RefreshTokenRepository;
import com.eventorback.auth.utils.JwtUtils;
import com.eventorback.global.config.EventorProperties;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

/**
 * 액세스·리프레시 토큰 발급과 재발급(회전).
 * <p>
 * 리프레시 토큰은 재발급할 때마다 새 값으로 바뀐다. 브라우저가 동시에 여러 요청을 보내 같은 토큰으로
 * 재발급을 시도하면 첫 요청만 회전하고, {@link #ROTATION_GRACE} 안에 들어온 나머지는 액세스 토큰만 새로 받는다.
 * 그 시간이 지난 교체된 토큰이 다시 오면 탈취로 보고 그 회원의 토큰을 모두 지운다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
	static final Duration ROTATION_GRACE = Duration.ofSeconds(30);
	private static final SecureRandom RANDOM = new SecureRandom();

	private final RefreshTokenRepository refreshTokenRepository;
	private final UserRepository userRepository;
	private final JwtUtils jwtUtils;
	private final EventorProperties properties;

	/**
	 * @param refreshToken 새로 발급한 리프레시 토큰. 회전하지 않았으면 null(쿠키를 바꾸지 않는다)
	 */
	public record Session(Long userId, List<String> roles, String accessToken, String refreshToken) {
	}

	@Transactional
	public Session issue(Long userId, List<String> roles) {
		String refreshToken = newRefreshToken(userId, LocalDateTime.now().plus(properties.jwt().refreshTtl()));
		return new Session(userId, roles, accessToken(userId, roles), refreshToken);
	}

	/**
	 * 리프레시 토큰으로 새 토큰을 받는다. 쓸 수 없는 토큰이면 빈 값.
	 */
	@Transactional
	public Optional<Session> refresh(String rawRefreshToken) {
		LocalDateTime now = LocalDateTime.now();
		Optional<RefreshToken> found = refreshTokenRepository.findByTokenHash(hash(rawRefreshToken));
		if (found.isEmpty()) {
			return Optional.empty();
		}

		RefreshToken token = found.get();
		if (token.isExpired(now)) {
			refreshTokenRepository.delete(token);
			return Optional.empty();
		}

		Optional<User> user = userRepository.findById(token.getUserId())
			.filter(u -> !"탈퇴".equals(u.getStatus().getName()));
		if (user.isEmpty()) {
			refreshTokenRepository.deleteAllByUserId(token.getUserId());
			return Optional.empty();
		}
		List<String> roles = roles(user.get());

		if (token.isRotated()) {
			if (token.isWithinGrace(now, ROTATION_GRACE)) {
				return Optional.of(new Session(token.getUserId(), roles, accessToken(token.getUserId(), roles), null));
			}
			log.warn("교체된 리프레시 토큰 재사용 감지: userId={}", token.getUserId());
			refreshTokenRepository.deleteAllByUserId(token.getUserId());
			return Optional.empty();
		}

		// 회전: 남은 유효 기간을 그대로 이어받는다(재발급만으로 로그인이 무한히 연장되지 않게)
		token.markRotated(now);
		String newRefreshToken = newRefreshToken(token.getUserId(), token.getExpiresAt());
		return Optional.of(new Session(token.getUserId(), roles, accessToken(token.getUserId(), roles), newRefreshToken));
	}

	@Transactional
	public void revoke(String rawRefreshToken) {
		refreshTokenRepository.deleteByTokenHash(hash(rawRefreshToken));
	}

	@Transactional
	public void revokeAll(Long userId) {
		refreshTokenRepository.deleteAllByUserId(userId);
	}

	/** 만료된 리프레시 토큰 정리(매일 04:10) */
	@Transactional
	@Scheduled(cron = "0 10 4 * * *")
	@SchedulerLock(name = "refreshToken.cleanup", lockAtMostFor = "10m")
	public void deleteExpired() {
		int deleted = refreshTokenRepository.deleteExpired(LocalDateTime.now());
		log.info("만료된 리프레시 토큰 {}개 삭제", deleted);
	}

	public static List<String> roles(User user) {
		return user.getUserRoles().stream().map(userRole -> userRole.getRole().getName()).toList();
	}

	private String accessToken(Long userId, List<String> roles) {
		return jwtUtils.generateAccessToken(userId, roles, properties.jwt().accessTtl());
	}

	private String newRefreshToken(Long userId, LocalDateTime expiresAt) {
		byte[] bytes = new byte[32];
		RANDOM.nextBytes(bytes);
		String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
		refreshTokenRepository.save(new RefreshToken(userId, hash(raw), expiresAt));
		return raw;
	}

	static String hash(String raw) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
}
