package com.eventorback.comment.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.eventorback.comment.domain.dto.request.CreateCommentRequest;
import com.eventorback.comment.domain.dto.request.UpdateCommentRequest;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.user.domain.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long commentId;

	@ManyToOne
	@JoinColumn(name = "parent_comment_id")
	private Comment parentComment;

	@OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> childrenComments = new ArrayList<>(); // 자식 댓글들(대댓글)

	@ManyToOne(optional = false)
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "status_id")
	private Status status;

	@Column(name = "writer")
	private String writer;

	@Column(name = "content")
	private String content;

	@Column(name = "recommendation_count")
	private Long recommendationCount;

	@Column(name = "decommendation_count")
	private Long decommendationCount;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Builder
	public Comment(Comment parentComment, Post post, User user, Status status, String writer, String content) {
		this.parentComment = parentComment;
		this.post = post;
		this.user = user;
		this.status = status;
		this.writer = writer;
		this.content = content;
		this.recommendationCount = 0L;
		this.decommendationCount = 0L;
		this.createdAt = LocalDateTime.now();
	}

	public static Comment toEntity(CreateCommentRequest request, Comment parentComment, Post post, User user,
		Status status) {
		return Comment.builder()
			.parentComment(parentComment)
			.post(post)
			.user(user)
			.status(status)
			.writer(user.getNickname())
			.content(request.content())
			.build();
	}

	public void updateComment(UpdateCommentRequest request) {
		this.content = request.content();
	}

	public void recommendComment() {
		this.recommendationCount++;
	}

	public void disrecommendComment() {
		this.decommendationCount++;
	}
}
