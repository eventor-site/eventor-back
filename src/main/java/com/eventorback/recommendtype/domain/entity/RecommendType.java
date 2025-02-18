package com.eventorback.recommendtype.domain.entity;

import com.eventorback.recommendtype.domain.dto.RecommendTypeDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "recommend_types")
public class RecommendType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recommend_type_id")
	private Long recommendTypeId;

	@Column(name = "name")
	private String name;

	@Builder
	public RecommendType(String name) {
		this.name = name;
	}

	public static RecommendType toEntity(RecommendTypeDto request) {
		return RecommendType.builder()
			.name(request.name())
			.build();
	}

	public static RecommendType toEntityFindOrCreate(String name) {
		return RecommendType.builder()
			.name(name)
			.build();
	}

	public void updateRecommendType(String name) {
		this.name = name;
	}

}
