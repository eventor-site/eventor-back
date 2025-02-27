package com.eventorback.commentreport.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.reporttype.domain.entity.ReportType;
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
@Table(name = "comment_reports")
public class CommentReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_report_id")
	private Long commentReportId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "comment_id")
	@OnDelete(action = OnDeleteAction.CASCADE) // DB 에서 자동 삭제 처리
	private Comment comment;

	@ManyToOne(optional = false)
	@JoinColumn(name = "report_type")
	private ReportType reportType;

	@Column(name = "is_checked")
	private boolean isChecked;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Builder
	public CommentReport(User user, Comment comment, ReportType reportType) {
		this.user = user;
		this.comment = comment;
		this.reportType = reportType;
		this.isChecked = false;
		this.createdAt = LocalDateTime.now();
	}

	public static CommentReport toEntity(User user, Comment comment, ReportType reportType) {
		return CommentReport.builder()
			.user(user)
			.comment(comment)
			.reportType(reportType)
			.build();
	}

	public void confirm() {
		this.isChecked = true;
	}
}
