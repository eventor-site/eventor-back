package com.eventorback.mail.service;

import jakarta.mail.internet.MimeMessage;

public interface MailService {

	MimeMessage createMail(String receiverEmail, String subject, String text);

	MimeMessage recoverIdentifierMail(String receiverEmail, String subject, String identifier);

	MimeMessage recoverPasswordMail(String receiverEmail, String subject, String password);

	void sendMail(String email, String subject, String text);

	boolean checkEmail(String email, String certifyCode, String subject);

	String getSubjectKey(String subject);

}
