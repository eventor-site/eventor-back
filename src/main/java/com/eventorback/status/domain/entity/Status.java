package com.eventorback.status.domain.entity;

import com.eventorback.status.domain.dto.request.StatusRequest;
import com.eventorback.statustype.domain.entity.StatusType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Status {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "status_id")
	private Long statusId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "status_type_id")
	private StatusType statusType;

	@Column(name = "name")
	private String name;

	@Builder
	public Status(StatusType statusType, String name) {
		this.statusType = statusType;
		this.name = name;
	}

	public static Status toEntity(StatusType statusType, StatusRequest request) {
		return Status.builder()
			.statusType(statusType)
			.name(request.name())
			.build();
	}

	public void updateName(StatusType statusType, String name) {
		this.statusType = statusType;
		this.name = name;
	}

}
