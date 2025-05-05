package com.eventorback.pointhistory.domain.entity;

import java.time.LocalDateTime;

import com.eventorback.point.domain.entity.Point;
import com.eventorback.user.domain.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "point_histories")
public class PointHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_history_id")
	private Long pointId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "point_id")
	private Point point;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Builder
	public PointHistory(User user, Point point) {
		this.user = user;
		this.point = point;
		this.createdAt = LocalDateTime.now();
	}

	public static PointHistory toEntity(User user, Point point) {
		return PointHistory.builder()
			.user(user)
			.point(point)
			.build();
	}

}
