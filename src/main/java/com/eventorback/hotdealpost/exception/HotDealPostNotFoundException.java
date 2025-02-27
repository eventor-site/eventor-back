package com.eventorback.hotdealpost.exception;

import com.eventorback.global.exception.NotFoundException;

public class HotDealPostNotFoundException extends NotFoundException {
	public HotDealPostNotFoundException() {
		super("핫딜 게시물을 찾을 수 없습니다.");
	}
}
