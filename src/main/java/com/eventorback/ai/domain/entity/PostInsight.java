package com.eventorback.ai.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시물 AI 분석 결과(post_insights). 쓰기는 {@code InsightPipeline} 이 SQL 로 하고, 엔티티는 조회·검색 조건용이다.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_insights")
public class PostInsight {
	@Id
	@Column(name = "post_id")
	private Long postId;

	@Column(name = "domain", length = 20)
	private String domain;

	@Column(name = "benefit_types", length = 120)
	private String benefitTypes;

	@Column(name = "brand_id")
	private Long brandId;

	@Column(name = "confidence", precision = 3, scale = 2)
	private BigDecimal confidence;

	@Column(name = "source_hash", length = 64, columnDefinition = "char(64)")
	private String sourceHash;

	@Column(name = "classified_at")
	private LocalDateTime classifiedAt;

	@Column(name = "classify_model", length = 40)
	private String classifyModel;

	@Column(name = "summary", length = 600)
	private String summary;

	@Column(name = "target", length = 200)
	private String target;

	@Column(name = "how_to", length = 300)
	private String howTo;

	@Column(name = "difficulty", length = 10)
	private String difficulty;

	@Column(name = "value_note", length = 120)
	private String valueNote;

	@Column(name = "live_status", length = 10)
	private String liveStatus;

	@Column(name = "live_reason", length = 200)
	private String liveReason;

	@Column(name = "deep_status", length = 10)
	private String deepStatus;

	@Column(name = "deep_attempts", nullable = false)
	private int deepAttempts;

	@Column(name = "deep_checked_at")
	private LocalDateTime deepCheckedAt;

	@Column(name = "next_deep_at")
	private LocalDateTime nextDeepAt;

	@Column(name = "deep_model", length = 40)
	private String deepModel;

	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime updatedAt;
}
