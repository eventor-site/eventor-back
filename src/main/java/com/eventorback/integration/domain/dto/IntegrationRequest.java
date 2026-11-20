package com.eventorback.integration.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 관리자가 연동 정보를 등록·수정할 때. 키 값은 받지 않는다(환경 변수 이름만). */
public record IntegrationRequest(
	@NotBlank @Size(max = 40) @Pattern(regexp = "[a-z0-9-]+", message = "코드는 영문 소문자·숫자·- 만 쓸 수 있어요.") String code,
	@NotBlank @Size(max = 60) String name,
	@NotBlank @Size(max = 60) String provider,
	@NotBlank @Size(max = 20) String category,
	@NotBlank @Size(max = 300) String purpose,
	@NotBlank @Pattern(regexp = "사용 중|준비 중|종료", message = "상태는 사용 중·준비 중·종료 중 하나예요.") String status,
	@NotBlank @Pattern(regexp = "개인 가능|사업자 필요|확인 필요", message = "이용 자격은 개인 가능·사업자 필요·확인 필요 중 하나예요.") String eligibility,
	@Size(max = 300) String eligibilityNote,
	@Size(max = 60) @Pattern(regexp = "^$|[a-z0-9-]+\\.png", message = "아이콘은 public/integrations/ 아래 png 파일 이름이에요.") String icon,
	@Size(max = 300) @Pattern(regexp = "^$|https?://.+", message = "주소는 http(s):// 로 시작해야 해요.") String siteUrl,
	@Size(max = 300) @Pattern(regexp = "^$|https?://.+", message = "주소는 http(s):// 로 시작해야 해요.") String consoleUrl,
	@Size(max = 300) @Pattern(regexp = "^$|https?://.+", message = "주소는 http(s):// 로 시작해야 해요.") String docsUrl,
	@Size(max = 200) @Pattern(regexp = "^$|[A-Z0-9_]+(\\s*,\\s*[A-Z0-9_]+)*", message = "환경 변수 이름을 쉼표로 구분해 적어요(예: TOUR_KEY).") String envKeys,
	@Size(max = 500) String memo,
	Integer sortOrder
) {
}
