package com.eventorback.auth.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.global.exception.BadRequestException;
import com.eventorback.global.util.NumberUtil;
import com.eventorback.global.util.PasswordUtil;
import com.eventorback.mail.service.MailService;
import com.eventorback.user.domain.dto.request.CertifyEmailRequest;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * 아이디 찾기·비밀번호 재설정.
 * <p>
 * 이전에는 아이디만 알면 누구나 그 계정의 비밀번호를 새 값으로 바꿔 메일로 보낼 수 있었다.
 * 이제는 계정 이메일로 받은 인증번호를 입력해야 비밀번호를 바꿀 수 있고, 새 비밀번호는 사용자가 정한다.
 */
@Service
@RequiredArgsConstructor
public class AccountRecoveryService {
	private static final String PURPOSE = "비밀번호 찾기";

	private final UserRepository userRepository;
	private final MailService mailService;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;

	/** 아이디 찾기. 가입 여부를 알려주지 않도록 결과와 관계없이 같은 문구를 돌려준다 */
	@Transactional(readOnly = true)
	public String findIdentifiers(String email) {
		if (email == null || email.isBlank()) {
			throw new BadRequestException("이메일을 입력해 주세요.");
		}
		List<String> identifiers = userRepository.findLinkCandidatesByEmail(email.trim()).stream()
			.map(User::getIdentifier)
			.filter(id -> id != null && !id.isBlank() && !id.startsWith("["))
			.toList();
		if (!identifiers.isEmpty()) {
			mailService.sendIdentifiers(email.trim(), identifiers);
		}
		return "가입된 아이디가 있으면 " + email.trim() + " 로 보내 드렸어요. 메일함을 확인해 주세요.";
	}

	/** 비밀번호 재설정 인증번호를 계정 이메일로 보낸다 */
	@Transactional(readOnly = true)
	public String sendResetCode(String identifier) {
		User user = findUser(identifier);
		if (user.getEmail() == null || user.getEmail().isBlank()) {
			throw new BadRequestException("이메일이 등록되지 않은 계정입니다. 관리자에게 문의해 주세요.");
		}
		boolean sent = mailService.sendMail(user.getEmail(), PURPOSE, NumberUtil.createRandom(6));
		String masked = maskEmail(user.getEmail());
		return sent ? masked + " 로 인증번호를 보냈어요. 3분 안에 입력해 주세요."
			: "이미 " + masked + " 로 인증번호를 보냈어요. 잠시 후 다시 시도해 주세요.";
	}

	/** 인증번호를 확인하고 새 비밀번호로 바꾼다. 다른 기기의 로그인은 모두 끊는다 */
	@Transactional
	public void resetPassword(String identifier, String code, String newPassword) {
		User user = findUser(identifier);
		if (newPassword == null || !PasswordUtil.isValidPassword(newPassword)) {
			throw new BadRequestException("비밀번호는 영문, 숫자, 특수문자를 포함해 8자 이상이어야 합니다.");
		}
		if (!mailService.certifyEmail(new CertifyEmailRequest(user.getEmail(), PURPOSE, code))) {
			throw new BadRequestException("인증번호가 맞지 않거나 시간이 지났어요. 다시 받아 주세요.");
		}
		user.modifyPassword(passwordEncoder.encode(newPassword));
		tokenService.revokeAll(user.getUserId());
	}

	private User findUser(String identifier) {
		if (identifier == null || identifier.isBlank()) {
			throw new BadRequestException("아이디를 입력해 주세요.");
		}
		return userRepository.findByIdentifier(identifier.trim())
			.filter(u -> u.getPassword() != null)
			.orElseThrow(() -> new BadRequestException("아이디로 가입된 계정을 찾을 수 없어요. 소셜 로그인 계정은 구글로 로그인해 주세요."));
	}

	static String maskEmail(String email) {
		int at = email.indexOf('@');
		if (at <= 1) {
			return "*" + email.substring(Math.max(at, 0));
		}
		String local = email.substring(0, at);
		int keep = Math.min(3, Math.max(1, local.length() / 3));
		return local.substring(0, keep) + "*".repeat(local.length() - keep) + email.substring(at);
	}
}
