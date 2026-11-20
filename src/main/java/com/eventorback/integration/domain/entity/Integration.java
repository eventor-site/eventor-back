package com.eventorback.integration.domain.entity;

import java.time.LocalDateTime;

import com.eventorback.integration.domain.dto.IntegrationRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 외부 연동(API·콘솔) 한 건. 키 값은 저장하지 않고 환경 변수 이름({@link #envKeys})만 둔다.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "integrations")
public class Integration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "integration_id")
	private Long integrationId;

	@Column(name = "code", nullable = false, length = 40)
	private String code;

	@Column(name = "name", nullable = false, length = 60)
	private String name;

	@Column(name = "provider", nullable = false, length = 60)
	private String provider;

	@Column(name = "category", nullable = false, length = 20)
	private String category;

	@Column(name = "purpose", nullable = false, length = 300)
	private String purpose;

	@Column(name = "status", nullable = false, length = 10)
	private String status;

	@Column(name = "eligibility", nullable = false, length = 10)
	private String eligibility;

	@Column(name = "eligibility_note", length = 300)
	private String eligibilityNote;

	@Column(name = "icon", length = 60)
	private String icon;

	@Column(name = "site_url", length = 300)
	private String siteUrl;

	@Column(name = "console_url", length = 300)
	private String consoleUrl;

	@Column(name = "docs_url", length = 300)
	private String docsUrl;

	@Column(name = "env_keys", length = 200)
	private String envKeys;

	@Column(name = "memo", length = 500)
	private String memo;

	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	@Column(name = "last_checked_at")
	private LocalDateTime lastCheckedAt;

	@Column(name = "last_check_ok")
	private Boolean lastCheckOk;

	@Column(name = "last_check_message", length = 300)
	private String lastCheckMessage;

	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime updatedAt;

	public static Integration create(IntegrationRequest request) {
		Integration integration = new Integration();
		integration.code = request.code().trim();
		integration.apply(request);
		return integration;
	}

	/** 코드는 점검 기능과 연결되므로 만든 뒤에는 바꾸지 않는다 */
	public void apply(IntegrationRequest request) {
		this.name = request.name().trim();
		this.provider = request.provider().trim();
		this.category = request.category().trim();
		this.purpose = request.purpose().trim();
		this.status = request.status();
		this.eligibility = request.eligibility();
		this.eligibilityNote = blankToNull(request.eligibilityNote());
		this.icon = blankToNull(request.icon());
		this.siteUrl = blankToNull(request.siteUrl());
		this.consoleUrl = blankToNull(request.consoleUrl());
		this.docsUrl = blankToNull(request.docsUrl());
		this.envKeys = blankToNull(request.envKeys());
		this.memo = blankToNull(request.memo());
		this.sortOrder = request.sortOrder() == null ? 0 : request.sortOrder();
	}

	public void recordCheck(boolean ok, String message) {
		this.lastCheckedAt = LocalDateTime.now();
		this.lastCheckOk = ok;
		this.lastCheckMessage = message == null ? null : message.substring(0, Math.min(message.length(), 300));
	}

	private static String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
