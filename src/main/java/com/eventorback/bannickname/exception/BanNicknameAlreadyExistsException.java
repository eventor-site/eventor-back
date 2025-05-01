package com.eventorback.bannickname.exception;

import com.eventorback.global.exception.AlreadyExistsException;

public class BanNicknameAlreadyExistsException extends AlreadyExistsException {
	public BanNicknameAlreadyExistsException() {
		super("이미 금지한 닉네임 입니다.");
	}
}
