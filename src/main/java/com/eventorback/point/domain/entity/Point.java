package com.eventorback.point.domain.entity;

import com.eventorback.point.domain.dto.request.PointRequest;

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
@Table(name = "points")
public class Point {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_id")
	private Long pointId;

	@Column(name = "name", length = 30)
	private String name;

	@Column(name = "amount")
	private Long amount;

	@Builder
	public Point(String name, Long amount) {
		this.name = name;
		this.amount = amount;
	}

	public static Point toEntity(PointRequest request) {
		return Point.builder()
			.name(request.name())
			.amount(request.amount())
			.build();
	}

	public void update(PointRequest request) {
		this.name = request.name();
		this.amount = request.amount();
	}

}
