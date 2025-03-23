package com.eventorback.mail.service.impl;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
	private final RedisTemplate<String, Object> redisTemplate;
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

	@Override
	public boolean sendMail(String email, String type, String text) {
		String key = getSubjectKey(type) + email;

		Object existsValue = redisTemplate.opsForValue().get(key);

		if (existsValue != null) {
			return false;
		}

		MimeMessage message = switch (type) {
			case "비밀번호 초기화" -> recoverPasswordMail(email, type, text);
			default -> createMail(email, type, text);
		};

		javaMailSender.send(message);

		redisTemplate.opsForValue().set(key, text, Duration.ofMinutes(3));

		return true;
	}

	@Override
	public boolean certifyEmail(CertifyEmailRequest request) {
		String key = getSubjectKey(request.type()) + request.email();
		String savedCode = (String)redisTemplate.opsForValue().getAndDelete(key);

		return savedCode != null && savedCode.equals(request.certifyCode().trim());
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
