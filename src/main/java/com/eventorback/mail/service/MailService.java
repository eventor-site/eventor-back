package com.eventorback.mail.service;

import jakarta.mail.internet.MimeMessage;

public interface MailService {

	void createNumber();

	MimeMessage createMail(String receiverEmail, String subject);

	void sendMail(String email, String subject);

	boolean checkEmail(String email, String certifyCode, String subject);

	String getSubjectKey(String subject);
}
