package com.eventorback.post.exception;

import com.eventorback.global.exception.NotFoundException;

public class PostNotFoundException extends NotFoundException {
	public PostNotFoundException() {
		super("게시물을 찾을 수 없습니다.");
	}
}
