package com.eventorback.postreport.domain.entity;

import java.time.LocalDateTime;

import com.eventorback.post.domain.entity.Post;
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
public class PostReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_report_id")
	private Long postReportId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne(optional = false)
	@JoinColumn(name = "report_type")
	private ReportType reportType;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Builder
	public PostReport(User user, Post post, ReportType reportType) {
		this.user = user;
		this.post = post;
		this.reportType = reportType;
		this.createdAt = LocalDateTime.now();
	}

	public static PostReport toEntity(User user, Post post, ReportType reportType) {
		return PostReport.builder()
			.user(user)
			.post(post)
			.reportType(reportType)
			.build();
	}
}
