package com.eventorback.tour.domain.dto.response;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
	String tel,               // 전화번호
	String eventStatusName,   // 상태: 예정, 진행중, 마감, 미정
	Integer remainingDay,      // 남은 일수 (오늘 기준 종료일까지 D-day)
	LocalDate rawEventStartDate // 정렬용 내부 필드
) {

	public static int getStatusPriority(String status) {
		return switch (status) {
			case "진행중" -> 1;
			case "예정" -> 2;
			case "마감" -> 3;
			default -> 4; // "미정" 또는 예외
		};
	}

	public static LocalDate parseDate(String dateStr, DateTimeFormatter formatter) {
		try {
			return (dateStr != null && !dateStr.isBlank()) ? LocalDate.parse(dateStr, formatter) : null;
		} catch (Exception e) {
			return null;
		}
	}

	public static String calculateEventStatus(LocalDate start, LocalDate end, LocalDate now) {
		if (start != null && now.isBefore(start)) {
			return "예정";
		} else if (start != null && end != null &&
			(now.isEqual(start) || now.isAfter(start)) && !now.isAfter(end)) {
			return "진행중";
		} else if (end != null && now.isAfter(end)) {
			return "마감";
		} else {
			return "미정";
		}
	}

	public static String formatWithDayOfWeek(LocalDate date) {
		if (date == null)
			return null;

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		DayOfWeek dayOfWeek = date.getDayOfWeek();
		String[] daysKor = {"월", "화", "수", "목", "금", "토", "일"};
		// dayOfWeek.getValue(): 월=1 ~ 일=7
		return date.format(formatter) + "(" + daysKor[dayOfWeek.getValue() - 1] + ")";
	}
}