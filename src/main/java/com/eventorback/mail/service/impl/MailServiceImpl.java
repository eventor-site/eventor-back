package com.eventorback.mail.service.impl;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.eventorback.mail.service.MailService;
import com.eventorback.user.domain.dto.request.CertifyEmailRequest;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 이메일 관련 기능을 구현하는 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
	private final JavaMailSender javaMailSender;
	private static final Duration CODE_TTL = Duration.ofMinutes(3);
	private static final String SIGNUP_VERIFIED = "SignUpVerified:";
	private final JdbcTemplate jdbcTemplate;
	@Value("${spring.mail.username}")
	private String senderEmail;

	@Override
	public MimeMessage createMail(String receiverEmail, String type, String text) {
		MimeMessage message = javaMailSender.createMimeMessage();

		try {
			message.setFrom(senderEmail);
			message.setRecipients(MimeMessage.RecipientType.TO, receiverEmail);
			message.setSubject("[Eventor] " + type + " 인증번호");
			String body = "";
			body += "<h1>인증번호: " + text + "</h1>";
			body += "<h3>3분 내로 인증번호를 입력해주시기 바랍니다.</h3>";
			body += "<h3>감사합니다.</h3>";
			message.setText(body, "UTF-8", "html");
		} catch (MessagingException e) {
			log.error("메일 생성 실패", e);
		}

		return message;
	}

	// @Override
	// public MimeMessage recoverIdentifierMail(String receiverEmail, String type, String identifier) {
	// 	MimeMessage message = javaMailSender.createMimeMessage();
	//
	// 	try {
	// 		message.setFrom(senderEmail);
	// 		message.setRecipients(MimeMessage.RecipientType.TO, receiverEmail);
	// 		message.setSubject("[Eventor] " + type);
	// 		String body = "";
	// 		body += "<h1>가입된 아이디: " + identifier + "</h1>";
	// 		body += "<h3>감사합니다.</h3>";
	// 		message.setText(body, "UTF-8", "html");
	// 	} catch (MessagingException e) {
	// 		log.error("메일 생성 실패", e);
	// 	}
	//
	// 	return message;
	// }

	@Override
	public MimeMessage recoverPasswordMail(String receiverEmail, String type, String password) {
		MimeMessage message = javaMailSender.createMimeMessage();

		try {
			message.setFrom(senderEmail);
			message.setRecipients(MimeMessage.RecipientType.TO, receiverEmail);
			message.setSubject("[Eventor] " + type);
			String body = "";
			body += "<h1>새로운 비밀번호: " + password + "</h1>";
			body += "<h3>바뀐 비밀번호로 로그인 후 새로운 비밀번호로 바꾸시기 바랍니다.</h3>";
			body += "<h3>감사합니다.</h3>";
			message.setText(body, "UTF-8", "html");
		} catch (MessagingException e) {
			log.error("메일 생성 실패", e);
		}

		return message;
	}

	/**
	 * 인증 메일을 보내고 코드(해시)를 3분 동안 DB에 둔다. 같은 용도·이메일로 3분 안에 다시 요청하면 보내지 않는다.
	 */
	@Override
	@Transactional
	public boolean sendMail(String email, String type, String text) {
		String purpose = getSubjectKey(type);
		LocalDateTime now = LocalDateTime.now();

		List<Timestamp> existing = jdbcTemplate.queryForList(
			"SELECT expires_at FROM email_verifications WHERE purpose = ? AND email = ?", Timestamp.class, purpose, email);
		if (!existing.isEmpty() && existing.getFirst().toLocalDateTime().isAfter(now)) {
			return false;
		}

		MimeMessage message = switch (type) {
			case "비밀번호 초기화" -> recoverPasswordMail(email, type, text);
			default -> createMail(email, type, text);
		};
		javaMailSender.send(message);

		jdbcTemplate.update("""
			INSERT INTO email_verifications (purpose, email, code, expires_at, created_at) VALUES (?, ?, ?, ?, ?)
			ON DUPLICATE KEY UPDATE code = VALUES(code), expires_at = VALUES(expires_at), created_at = VALUES(created_at)""",
			purpose, email, sha256(text), Timestamp.valueOf(now.plus(CODE_TTL)), Timestamp.valueOf(now));
		return true;
	}

	/**
	 * 코드를 확인한다. 맞든 틀리든 코드는 지운다(한 번 보낸 코드로는 한 번만 시도할 수 있다).
	 */
	@Override
	@Transactional
	public boolean certifyEmail(CertifyEmailRequest request) {
		String purpose = getSubjectKey(request.type());
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(
			"SELECT code, expires_at FROM email_verifications WHERE purpose = ? AND email = ? FOR UPDATE",
			purpose, request.email());
		jdbcTemplate.update("DELETE FROM email_verifications WHERE purpose = ? AND email = ?", purpose, request.email());

		if (rows.isEmpty() || request.certifyCode() == null) {
			return false;
		}
		Map<String, Object> row = rows.getFirst();
		LocalDateTime expiresAt = ((Timestamp)row.get("expires_at")).toLocalDateTime();
		boolean ok = expiresAt.isAfter(LocalDateTime.now())
			&& MessageDigest.isEqual(
			String.valueOf(row.get("code")).getBytes(StandardCharsets.UTF_8),
			sha256(request.certifyCode().trim()).getBytes(StandardCharsets.UTF_8));

		// 회원가입 인증을 마치면 30분 동안 가입할 수 있다는 기록을 남긴다(가입 API가 확인한다)
		if (ok && "회원가입".equals(request.type())) {
			LocalDateTime now = LocalDateTime.now();
			jdbcTemplate.update("""
				INSERT INTO email_verifications (purpose, email, code, expires_at, created_at) VALUES (?, ?, 'verified', ?, ?)
				ON DUPLICATE KEY UPDATE code = 'verified', expires_at = VALUES(expires_at), created_at = VALUES(created_at)""",
				SIGNUP_VERIFIED, request.email(), Timestamp.valueOf(now.plusMinutes(30)), Timestamp.valueOf(now));
		}
		return ok;
	}

	@Override
	@Transactional
	public boolean consumeSignupVerified(String email) {
		int deleted = jdbcTemplate.update(
			"DELETE FROM email_verifications WHERE purpose = ? AND email = ? AND expires_at > ?",
			SIGNUP_VERIFIED, email, Timestamp.valueOf(LocalDateTime.now()));
		return deleted > 0;
	}

	@Override
	public void sendIdentifiers(String email, List<String> identifiers) {
		MimeMessage message = javaMailSender.createMimeMessage();
		try {
			message.setFrom(senderEmail);
			message.setRecipients(MimeMessage.RecipientType.TO, email);
			message.setSubject("[Eventor] 아이디 찾기");
			StringBuilder body = new StringBuilder("<h3>이 이메일로 가입된 아이디입니다.</h3><ul>");
			identifiers.forEach(id -> body.append("<li><strong>").append(org.springframework.web.util.HtmlUtils.htmlEscape(id))
				.append("</strong></li>"));
			body.append("</ul><p>본인이 요청하지 않았다면 이 메일을 무시해 주세요.</p>");
			message.setText(body.toString(), "UTF-8", "html");
		} catch (MessagingException e) {
			log.error("메일 생성 실패", e);
			return;
		}
		javaMailSender.send(message);
	}

	/** 만료된 인증 코드 정리(매시 20분) */
	@Transactional
	@Scheduled(cron = "0 20 * * * *")
	@SchedulerLock(name = "mail.cleanupVerifications", lockAtMostFor = "5m")
	public void deleteExpiredVerifications() {
		jdbcTemplate.update("DELETE FROM email_verifications WHERE expires_at < ?", Timestamp.valueOf(LocalDateTime.now()));
	}

	private static String sha256(String value) {
		try {
			return HexFormat.of().formatHex(
				MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String getSubjectKey(String type) {
		String typeKey = "none:";
		if ("회원가입".equals(type)) {
			typeKey = "SignUpEmail:";
		} else if ("이메일 수정".equals(type)) {
			typeKey = "UpdateEmail:";
		} else if ("휴면계정 활성화".equals(type)) {
			typeKey = "DormantToActiveEmail:";
		} else if ("아이디 찾기".equals(type)) {
			typeKey = "FindIdentifier:";
		} else if ("비밀번호 찾기".equals(type)) {
			typeKey = "FindPassword:";
		} else if ("비밀번호 초기화".equals(type)) {
			typeKey = "ResetPassword:";
		}
		return typeKey;
	}

}
