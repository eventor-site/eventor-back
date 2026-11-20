package com.eventorback.auth.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.auth.dto.request.LoginRequest;
import com.eventorback.auth.dto.response.LoginResponse;
import com.eventorback.auth.google.GoogleOAuthClient.GoogleProfile;
import com.eventorback.auth.utils.AuthCookies;
import com.eventorback.auth.utils.JwtUtils;
import com.eventorback.global.exception.BadRequestException;
import com.eventorback.global.exception.UnauthorizedException;
import com.eventorback.status.repository.StatusRepository;
import com.eventorback.user.domain.dto.request.CheckNicknameRequest;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLoginAtRequest;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.repository.UserRepository;
import com.eventorback.user.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 로그인·로그아웃·휴면 해제·구글 로그인 처리.
 * <p>
 * 회원 상태별 처리
 * <ul>
 *     <li>탈퇴: 로그인 거부</li>
 *     <li>휴면: 토큰 대신 휴면 해제 티켓(auth_ticket 쿠키, 10분)을 주고, 사용자가 해제를 확인하면 활성으로 바꾼 뒤 로그인</li>
 *     <li>그 외(활성·정지): 로그인. 정지 회원의 기능 제한은 {@code @AuthorizeRole}이 한다</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AuthService {
	static final String TICKET_DORMANT = "dormant";
	static final String TICKET_GOOGLE_SIGNUP = "google-signup";
	private static final Duration TICKET_TTL = Duration.ofMinutes(10);
	private static final String GOOGLE = "google";

	private final UserRepository userRepository;
	private final UserService userService;
	private final StatusRepository statusRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;
	private final JwtUtils jwtUtils;
	private final AuthCookies authCookies;

	// ===== 아이디·비밀번호 로그인 =====

	@Transactional
	public LoginResponse login(LoginRequest request, HttpServletResponse response) {
		if (request.identifier() == null || request.password() == null) {
			throw new UnauthorizedException("아이디 또는 비밀번호가 올바르지 않습니다.");
		}
		User user = userRepository.findByIdentifier(request.identifier().trim())
			.filter(u -> u.getPassword() != null && passwordEncoder.matches(request.password(), u.getPassword()))
			.orElseThrow(() -> new UnauthorizedException("아이디 또는 비밀번호가 올바르지 않습니다."));

		return completeLogin(user, response);
	}

	@Transactional
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		AuthCookies.read(request, AuthCookies.REFRESH).ifPresent(tokenService::revoke);
		authCookies.clearTokens(response);
		authCookies.clearTicket(response);
	}

	// ===== 휴면 해제 =====

	/**
	 * 휴면 해제 티켓으로 계정을 활성으로 바꾸고 로그인한다.
	 */
	@Transactional
	public void activateDormant(HttpServletRequest request, HttpServletResponse response) {
		Claims claims = readTicket(request, TICKET_DORMANT);
		User user = userRepository.findById(JwtUtils.userId(claims))
			.orElseThrow(() -> new UnauthorizedException("회원 정보를 찾을 수 없습니다."));
		if (!"휴면".equals(user.getStatus().getName())) {
			throw new BadRequestException("휴면 상태인 계정이 아닙니다.");
		}

		user.updateStatus(statusRepository.findOrCreateStatus("회원", "활성"));
		authCookies.clearTicket(response);
		completeLogin(user, response);
	}

	// ===== 구글 로그인 =====

	/** 구글 로그인 결과(콜백이 프론트의 어느 화면으로 보낼지) */
	public enum GoogleResult {
		LOGGED_IN, DORMANT, SIGNUP_REQUIRED, WITHDRAWN
	}

	/**
	 * 구글 프로필로 로그인한다.
	 * 1) 구글 계정이 연결된 회원 → 로그인
	 * 2) 같은 이메일(구글이 확인한 이메일)의 기존 회원(카카오·네이버·아이디) → 구글 계정을 연결하고 로그인
	 * 3) 없으면 가입 대기 티켓을 주고 닉네임 입력 화면으로
	 */
	@Transactional
	public GoogleResult googleLogin(GoogleProfile profile, HttpServletResponse response) {
		User user = userRepository.findFirstByOauthTypeAndOauthId(GOOGLE, profile.sub()).orElse(null);

		if (user == null && profile.emailVerified() && profile.email() != null) {
			List<User> candidates = userRepository.findLinkCandidatesByEmail(profile.email());
			if (!candidates.isEmpty()) {
				user = candidates.getFirst();
				user.linkOauth(GOOGLE, profile.sub());
			}
		}

		if (user == null) {
			String ticket = jwtUtils.generateTicket(TICKET_GOOGLE_SIGNUP, Map.of(
				"sub", profile.sub(),
				"email", profile.email() == null ? "" : profile.email(),
				"name", profile.name() == null ? "" : profile.name()), TICKET_TTL);
			authCookies.setTicket(response, ticket, TICKET_TTL);
			return GoogleResult.SIGNUP_REQUIRED;
		}

		if ("탈퇴".equals(user.getStatus().getName())) {
			return GoogleResult.WITHDRAWN;
		}
		LoginResponse result = completeLogin(user, response);
		return result == LoginResponse.DORMANT ? GoogleResult.DORMANT : GoogleResult.LOGGED_IN;
	}

	/** 가입 대기 중인 구글 계정 정보(닉네임 입력 화면 표시용) */
	public Map<String, String> googlePending(HttpServletRequest request) {
		Claims claims = readTicket(request, TICKET_GOOGLE_SIGNUP);
		return Map.of(
			"email", claims.get("email", String.class),
			"name", claims.get("name", String.class));
	}

	/**
	 * 닉네임을 받아 구글 회원으로 가입시키고 로그인한다.
	 */
	@Transactional
	public void googleSignup(String nickname, HttpServletRequest request, HttpServletResponse response) {
		Claims claims = readTicket(request, TICKET_GOOGLE_SIGNUP);
		String sub = claims.get("sub", String.class);
		String email = claims.get("email", String.class);

		if (userRepository.existsByOauthIdAndOauthType(sub, GOOGLE)) {
			throw new BadRequestException("이미 가입된 구글 계정입니다. 다시 로그인해 주세요.");
		}

		String trimmed = nickname == null ? "" : nickname.trim();
		if (trimmed.length() < 2 || trimmed.length() > 10) {
			throw new BadRequestException("닉네임은 2~10자로 입력해 주세요.");
		}
		String nicknameCheck = userService.checkNickname(new CheckNicknameRequest(trimmed));
		if (!nicknameCheck.startsWith("사용 가능")) {
			throw new BadRequestException(nicknameCheck);
		}

		userService.signup(SignUpRequest.builder()
			.nickname(trimmed)
			.email(email == null || email.isBlank() ? null : email)
			.oauthId(sub)
			.oauthType(GOOGLE)
			.build());

		User user = userRepository.findFirstByOauthTypeAndOauthId(GOOGLE, sub)
			.orElseThrow(() -> new IllegalStateException("가입한 회원을 찾을 수 없습니다."));
		authCookies.clearTicket(response);
		completeLogin(user, response);
	}

	/**
	 * 구글 로그인 state 값(위조 방지). 쿠키에 같은 값을 두고 콜백에서 비교한다.
	 */
	public static String newState() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	// ===== 공통 =====

	private LoginResponse completeLogin(User user, HttpServletResponse response) {
		String status = user.getStatus().getName();
		if ("탈퇴".equals(status)) {
			throw new UnauthorizedException("탈퇴한 회원입니다. 관리자에게 문의해 주세요.");
		}
		if ("휴면".equals(status)) {
			String ticket = jwtUtils.generateTicket(TICKET_DORMANT, Map.of("userId", user.getUserId()), TICKET_TTL);
			authCookies.setTicket(response, ticket, TICKET_TTL);
			return LoginResponse.DORMANT;
		}

		TokenService.Session session = tokenService.issue(user.getUserId(), TokenService.roles(user));
		authCookies.setTokens(response, session.accessToken(), session.refreshToken());
		userService.updateLoginAt(new UpdateLoginAtRequest(user.getUserId(), LocalDateTime.now()));
		return LoginResponse.LOGGED_IN;
	}

	private Claims readTicket(HttpServletRequest request, String type) {
		String ticket = AuthCookies.read(request, AuthCookies.TICKET)
			.orElseThrow(() -> new UnauthorizedException("인증 시간이 지났습니다. 다시 로그인해 주세요."));
		try {
			return jwtUtils.parse(ticket, type);
		} catch (JwtException | IllegalArgumentException e) {
			throw new UnauthorizedException("인증 시간이 지났습니다. 다시 로그인해 주세요.");
		}
	}
}
