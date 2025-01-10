package com.eventorback.mail.service.impl;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.eventorback.global.util.NumberUtil;
import com.eventorback.mail.service.MailService;

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
	public MimeMessage createMail(String receiverEmail, String subject, String text) {
		MimeMessage message = javaMailSender.createMimeMessage();

		try {
			message.setFrom(senderEmail);
			message.setRecipients(MimeMessage.RecipientType.TO, receiverEmail);
			message.setSubject("[Eventor] " + subject + " 인증번호");
			String body = "";
			body += "<h3>" + subject + " 인증번호입니다.</h3>";
			body += "<h1>인증번호: " + NumberUtil.createRandom(6) + "</h1>";
			body += "<h3>3분 내로 인증번호를 입력해주시기 바랍니다.</h3>";
			body += "<h3>감사합니다.</h3>";
			message.setText(body, "UTF-8", "html");
		} catch (MessagingException e) {
			log.error("메일 생성 실패", e);
		}

		return message;
	}

	@Override
	public MimeMessage recoverIdentifierMail(String receiverEmail, String subject, String identifier) {
		MimeMessage message = javaMailSender.createMimeMessage();

		try {
			message.setFrom(senderEmail);
			message.setRecipients(MimeMessage.RecipientType.TO, receiverEmail);
			message.setSubject("[Eventor] " + subject);
			String body = "";
			body += "<h1>가입된 아이디: " + identifier + "</h1>";
			body += "<h3>감사합니다.</h3>";
			message.setText(body, "UTF-8", "html");
		} catch (MessagingException e) {
			log.error("메일 생성 실패", e);
		}

		return message;
	}

	@Override
	public MimeMessage recoverPasswordMail(String receiverEmail, String subject, String password) {
		MimeMessage message = javaMailSender.createMimeMessage();

		try {
			message.setFrom(senderEmail);
			message.setRecipients(MimeMessage.RecipientType.TO, receiverEmail);
			message.setSubject("[Eventor] " + subject);
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
	public void sendMail(String email, String subject, String text) {
		MimeMessage message = null;
		if (subject.equals("회원가입") || subject.equals("휴면계정 활성화")) {
			message = createMail(email, subject, text);
		} else if ("아이디 찾기".equals(subject)) {
			message = recoverIdentifierMail(email, subject, text);
		} else if ("비밀번호 찾기".equals(subject)) {
			message = recoverPasswordMail(email, subject, text);
		} else {
			message = createMail(email, subject, text);
		}

		javaMailSender.send(message);

		String key = getSubjectKey(subject) + email;
		redisTemplate.opsForValue().set(key, text, Duration.ofMinutes(3));

	}

	@Override
	public boolean checkEmail(String email, String certifyCode, String subject) {
		String key = getSubjectKey(subject) + email;
		String savedCode = (String)redisTemplate.opsForValue().get(key);

		if (savedCode == null) {
			return false;
		}

		if (savedCode.equals(certifyCode)) {
			redisTemplate.delete(key);
			return true;
		}

		return false;
	}

	@Override
	public String getSubjectKey(String subject) {
		String subjectKey = "none:";
		if ("회원가입".equals(subject)) {
			subjectKey = "SignUpEmail:";
		} else if ("휴면계정 활성화".equals(subject)) {
			subjectKey = "DormantToActiveEmail:";
		} else if ("아이디 찾기".equals(subject)) {
			subjectKey = "FindIdentifier:";
		} else if ("비밀번호 찾기".equals(subject)) {
			subjectKey = "FindPassword:";
		}
		return subjectKey;
	}

}
