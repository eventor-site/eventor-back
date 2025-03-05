package com.eventorback.post.domain.entity;

import java.time.LocalDateTime;

import com.eventorback.category.domain.entity.Category;
import com.eventorback.event.domain.entity.Event;
import com.eventorback.hotdeal.domain.entity.HotDeal;
import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.user.domain.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

	@OneToOne(cascade = CascadeType.ALL)  // Post 삭제 시 Event 도 삭제됨
	@JoinColumn(name = "event_id")
	private Event event;

	@OneToOne(cascade = CascadeType.ALL)  // Post 삭제 시 HotDeal 도 삭제됨
	@JoinColumn(name = "hotdeal_id")
	private HotDeal hotDeal;

	@Column(name = "writer", length = 30)
	private String writer;

	@Column(name = "writer_grade")
	private String writerGrade;

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
	public Post(Category category, User user, Status status, HotDeal hotDeal, Event event, String writer,
		String writerGrade, String title,
		String content) {
		this.category = category;
		this.user = user;
		this.status = status;
		this.hotDeal = hotDeal;
		this.event = event;
		this.writer = writer;
		this.writerGrade = writerGrade;
		this.title = title;
		this.content = content;
		this.recommendationCount = 0L;
		this.viewCount = 0L;
		this.createdAt = LocalDateTime.now();
	}

	public static Post toEntity(Category category, User user, Status status, HotDeal hotDeal,
		CreatePostRequest request) {
		return Post.builder()
			.category(category)
			.user(user)
			.status(status)
			.hotDeal(hotDeal)
			.writer(user.getNickname())
			.writerGrade(user.getGrade().getName())
			.title(request.title())
			.content(request.content())
			.build();
	}

	public static Post toEntity(Category category, User user, Status status, Event event, CreatePostRequest request) {
		return Post.builder()
			.category(category)
			.user(user)
			.status(status)
			.event(event)
			.writer(user.getNickname())
			.writerGrade(user.getGrade().getName())
			.title(request.title())
			.content(request.content())
			.build();
	}

	public static Post toEntity(Category category, User user, Status status, CreatePostRequest request) {
		return Post.builder()
			.category(category)
			.user(user)
			.status(status)
			.writer(user.getNickname())
			.writerGrade(user.getGrade().getName())
			.title(request.title())
			.content(request.content())
			.build();
	}

	public void update(Category category, UpdatePostRequest request) {
		this.category = category;
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
