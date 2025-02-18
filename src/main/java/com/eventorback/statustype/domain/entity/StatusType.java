package com.eventorback.statustype.domain.entity;

import com.eventorback.statustype.domain.dto.StatusTypeDto;

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
@Table(name = "status_types")
public class StatusType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "status_type_id")
	private Long statusTypeId;

	@Column(name = "name")
	private String name;

	@Builder
	public StatusType(String name) {
		this.name = name;
	}

	public static StatusType toEntity(StatusTypeDto request) {
		return StatusType.builder()
			.name(request.name())
			.build();
	}

	public void updateStatusType(String name) {
		this.name = name;
	}

}
