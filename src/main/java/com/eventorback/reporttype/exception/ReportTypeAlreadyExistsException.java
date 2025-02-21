package com.eventorback.reporttype.exception;

import com.eventorback.global.exception.AlreadyExistsException;

public class ReportTypeAlreadyExistsException extends AlreadyExistsException {
	public ReportTypeAlreadyExistsException() {
		super("신고유형이 이미 존재합니다.");
	}
}
