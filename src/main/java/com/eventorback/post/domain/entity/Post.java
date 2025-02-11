package com.eventorback.post.domain.entity;

import java.time.LocalDateTime;

import com.eventorback.category.domain.entity.Category;
import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.status.domain.entity.Status;
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
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id")
	private Long postId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "status_id")
	private Status status;

	@Column(name = "writer")
	private String writer;

	@Column(name = "title")
	private String title;

	@Column(name = "content")
	private String content;

	@Column(name = "recommendation_count")
	private Long recommendationCount;

	@Column(name = "view_count")
	private Long viewCount;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "is_notification")
	private Boolean isNotification;

	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	@Builder
	public Post(Category category, User user, Status status, String writer, String title, String content,
		Boolean isNotification, LocalDateTime startTime, LocalDateTime endTime) {
		this.category = category;
		this.user = user;
		this.status = status;
		this.writer = writer;
		this.title = title;
		this.content = content;
		this.recommendationCount = 0L;
		this.viewCount = 0L;
		this.createdAt = LocalDateTime.now();
		this.isNotification = isNotification;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public static Post toEntity(Category category, User user, Status status, CreatePostRequest request) {
		return Post.builder()
			.category(category)
			.user(user)
			.status(status)
			.writer(user.getNickname())
			.title(request.title())
			.content(request.content())
			.isNotification(request.isNotification())
			.startTime(request.startTime())
			.endTime(request.endTime())
			.build();
	}

	public void update(UpdatePostRequest request) {
		this.title = request.title();
		this.content = request.content();
		this.isNotification = request.isNotification();
		this.startTime = request.startTime();
		this.endTime = request.endTime();
	}

	public void updatePostStatus(Status status) {
		this.status = status;
	}

	public void increaseViewCount() {
		this.viewCount++;
	}

	public void setDeletedAt() {
		this.deletedAt = LocalDateTime.now();
	}

	public void recommendPost() {
		this.recommendationCount++;
	}

	public void disrecommendPost() {
		this.recommendationCount--;
	}

}
