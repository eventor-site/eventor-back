package com.eventorback.statistic.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "statistics")
public class Statistic {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "statistic_id")
	private Long statisticId;

	@Column(name = "date")
	private LocalDate date;

	@Column(name = "visitor_count")
	private Long visitorCount;

	@Column(name = "main_view_count")
	private Long mainViewCount;

	@Column(name = "post_view_count")
	private Long postViewCount;

	@Column(name = "login_count")
	private Long loginCount;

	@Column(name = "signup_count")
	private Long signupCount;

	@Builder
	public Statistic() {
		this.date = LocalDate.now();
		this.visitorCount = 0L;
		this.mainViewCount = 0L;
		this.postViewCount = 0L;
		this.loginCount = 0L;
		this.signupCount = 0L;
	}

	public void increaseVisitorCount() {
		this.visitorCount++;
	}

	public void updateLoginCount(Long loginCount) {
		this.loginCount = loginCount;
	}

	public void increaseMainViewCount() {
		this.mainViewCount++;
	}

	public void increasePostViewCount() {
		this.postViewCount++;
	}

	public void increaseSignupCount() {
		this.signupCount++;
	}

}
