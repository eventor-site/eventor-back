package com.eventorback.bannickname.exception;

import com.eventorback.global.exception.NotFoundException;

public class BanNicknameNotFoundException extends NotFoundException {
	public BanNicknameNotFoundException() {
		super("금지 닉네임을 찾을 수 없습니다.");
	}
}
