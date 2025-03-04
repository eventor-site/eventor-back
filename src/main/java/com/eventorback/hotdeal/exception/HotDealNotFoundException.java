package com.eventorback.hotdeal.exception;

import com.eventorback.global.exception.NotFoundException;

public class HotDealNotFoundException extends NotFoundException {
	public HotDealNotFoundException() {
		super("핫딜을 찾을 수 없습니다.");
	}
}
