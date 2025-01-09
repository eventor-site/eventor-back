package com.eventorback.mail.service.impl;

import java.time.Duration;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
	private static final StringBuilder randomNumber = new StringBuilder(6);
	private final JavaMailSender javaMailSender;
	private final RedisTemplate<String, Object> redisTemplate;
	@Value("${spring.mail.username}")
	private String senderEmail;

	@Override
	public void createNumber() {
		for (int i = 0; i < 6; i++) {
			Random random = new Random();
			randomNumber.append(random.nextInt(10)); // 0부터 9까지 난수
		}
	}

	@Override
	public MimeMessage createMail(String receiverEmail, String subject) {
		createNumber();
		MimeMessage message = javaMailSender.createMimeMessage();

		try {
			message.setFrom(senderEmail);
			message.setRecipients(MimeMessage.RecipientType.TO, receiverEmail);
			message.setSubject("[Eventor] " + subject + " 인증번호");
			String body = "";
			body += "<h3>" + subject + " 인증번호입니다.</h3>";
			body += "<h1>인증번호: " + randomNumber + "</h1>";
			body += "<h3>3분 내로 인증번호를 입력해주시기 바랍니다.</h3>";
			body += "<h3>감사합니다.</h3>";
			message.setText(body, "UTF-8", "html");
		} catch (MessagingException e) {
			log.error("메일 생성 실패", e);
		}

		return message;
	}

	@Override
	public void sendMail(String email, String subject) {
		MimeMessage message = createMail(email, subject);
		javaMailSender.send(message);

		String key = getSubjectKey(subject) + email;
		redisTemplate.opsForValue().set(key, randomNumber, Duration.ofMinutes(3));

		randomNumber.setLength(0);
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
		}
		return subjectKey;
	}
}
