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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "posts")
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

	@Lob
	@Column(name = "content", columnDefinition = "TEXT")
	private String content;

	@Column(name = "recommendation_count")
	private Long recommendationCount;

	@Column(name = "view_count")
	private Long viewCount;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Builder
	public Post(Category category, User user, Status status, String writer, String title, String content) {
		this.category = category;
		this.user = user;
		this.status = status;
		this.writer = writer;
		this.title = title;
		this.content = content;
		this.recommendationCount = 0L;
		this.viewCount = 0L;
		this.createdAt = LocalDateTime.now();
	}

	public static Post toEntity(Category category, User user, Status status, CreatePostRequest request) {
		return Post.builder()
			.category(category)
			.user(user)
			.status(status)
			.writer(user.getNickname())
			.title(request.title())
			.content(request.content())
			.build();
	}

	public void update(UpdatePostRequest request) {
		this.title = request.title();
		this.content = request.content();
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
