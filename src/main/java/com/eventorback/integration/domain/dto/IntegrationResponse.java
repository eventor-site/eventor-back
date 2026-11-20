package com.eventorback.integration.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자 화면용 연동 정보.
 * envKeys 는 이름과 설정 여부만 담는다. 키 값(일부라도)은 절대 내보내지 않는다.
 */
public record IntegrationResponse(
	Long integrationId,
	String code,
	String name,
	String provider,
	String category,
	String purpose,
	String status,
	String eligibility,
	String eligibilityNote,
	String icon,
	String siteUrl,
	String consoleUrl,
	String docsUrl,
	List<EnvKey> envKeys,
	String memo,
	int sortOrder,
	boolean checkable,
	LocalDateTime lastCheckedAt,
	Boolean lastCheckOk,
	String lastCheckMessage,
	LocalDateTime updatedAt
) {
	public record EnvKey(String name, boolean configured) {
	}
}
