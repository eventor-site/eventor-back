package com.eventorback.mail.service;

import com.eventorback.user.domain.dto.request.CertifyEmailRequest;

import jakarta.mail.internet.MimeMessage;

public interface MailService {

	MimeMessage createMail(String receiverEmail, String type, String text);

	// MimeMessage recoverIdentifierMail(String receiverEmail, String type, String identifier);

	MimeMessage recoverPasswordMail(String receiverEmail, String type, String password);

	boolean sendMail(String email, String type, String text);

	boolean certifyEmail(CertifyEmailRequest request);

	String getSubjectKey(String type);

	/** 회원가입 이메일 인증을 마쳤는지 확인하고 그 기록을 지운다(가입 1회에 한 번) */
	boolean consumeSignupVerified(String email);

	/** 아이디 찾기: 그 이메일로 가입된 아이디 목록을 보낸다 */
	void sendIdentifiers(String email, java.util.List<String> identifiers);

}
