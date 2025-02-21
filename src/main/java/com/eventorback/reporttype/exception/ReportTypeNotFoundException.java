package com.eventorback.reporttype.exception;

import com.eventorback.global.exception.NotFoundException;

public class ReportTypeNotFoundException extends NotFoundException {
	public ReportTypeNotFoundException() {
		super("신고 유형을 찾을 수 없습니다.");
	}
}
