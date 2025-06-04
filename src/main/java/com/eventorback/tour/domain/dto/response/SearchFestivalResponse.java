package com.eventorback.tour.domain.dto.response;

import lombok.Builder;

@Builder
public record SearchFestivalResponse(
	String contentId,         // 내용 ID
	String contentTypeId,     // 내용 타입 ID
	String title,            // 축제 제목
	String addr1,            // 기본 주소
	String addr2,            // 상세 주소 또는 장소명
	String eventStartDate,   // 시작일 (yyyyMMdd)
	String eventEndDate,     // 종료일 (yyyyMMdd)
	String firstImage,       // 대표 이미지 URL
	String tel               // 전화번호
) {
}