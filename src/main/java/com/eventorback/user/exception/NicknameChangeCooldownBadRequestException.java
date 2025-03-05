package com.eventorback.user.exception;

import com.eventorback.global.exception.BadRequestException;

public class NicknameChangeCooldownBadRequestException extends BadRequestException {
	public NicknameChangeCooldownBadRequestException() {
		super("닉네임은 한 달 주기로 한 번만 변경할 수 있습니다.");
	}
}
