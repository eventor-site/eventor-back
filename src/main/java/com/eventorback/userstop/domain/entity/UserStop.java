package com.eventorback.userstop.domain.entity;

import java.time.LocalDateTime;

import com.eventorback.reporttype.domain.entity.ReportType;
import com.eventorback.user.domain.entity.User;
import com.eventorback.userstop.domain.dto.UserStopDto;

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
@Table(name = "user_stops")
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

	@Column(name = "stop_day")
	private Long stopDay;

	@Builder
	public UserStop(User user, ReportType reportType, LocalDateTime startTime, LocalDateTime endTime, Long stopDay) {
		this.user = user;
		this.reportType = reportType;
		this.startTime = startTime;
		this.endTime = endTime;
		this.stopDay = stopDay;
	}

	public static UserStop toEntity(User user, ReportType reportType, UserStopDto request) {
		// 시작 시간 설정 (현재 시간)
		LocalDateTime startTime = LocalDateTime.now();

		// 종료 시간 계산 (시작 시간 + 신고 유형에 저장된 일수)
		LocalDateTime endTime = startTime.plusDays(request.stopDay());

		return UserStop.builder()
			.user(user)
			.reportType(reportType)
			.startTime(startTime)
			.endTime(endTime)
			.stopDay(request.stopDay())
			.build();
	}

}
