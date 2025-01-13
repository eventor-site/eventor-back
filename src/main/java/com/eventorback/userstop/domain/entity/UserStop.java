package com.eventorback.userstop.domain.entity;

import java.time.LocalDateTime;

import com.eventorback.reporttype.domain.entity.ReportType;
import com.eventorback.user.domain.entity.User;

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
public class UserStop {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_stop_id")
	private Long userStopId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "report_type_id")
	private ReportType reportType;

	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	@Builder
	public UserStop(User user, ReportType reportType, LocalDateTime startTime, LocalDateTime endTime) {
		this.user = user;
		this.reportType = reportType;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public static UserStop toEntity(User user, ReportType reportType, LocalDateTime startTime, LocalDateTime endTime) {
		return UserStop.builder()
			.user(user)
			.reportType(reportType)
			.startTime(startTime)
			.endTime(endTime)
			.build();
	}

}
