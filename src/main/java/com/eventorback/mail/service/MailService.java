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

}
