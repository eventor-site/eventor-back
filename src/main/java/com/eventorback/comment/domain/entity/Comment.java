package com.eventorback.comment.domain.entity;

import java.time.LocalDateTime;

import com.eventorback.comment.domain.dto.request.CreateCommentRequest;
import com.eventorback.comment.domain.dto.request.UpdateCommentRequest;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.status.domain.entity.Status;
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
@Table(name = "comments")
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private Long commentId;

	@ManyToOne
	@JoinColumn(name = "parent_comment_id")
	private Comment parentComment;

	@ManyToOne(optional = false)
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "status_id")
	private Status status;

	@Column(name = "writer", length = 30)
	private String writer;

	@Column(name = "content", length = 1500)
	private String content;

	@Column(name = "recommendation_count")
	private Long recommendationCount;

	@Column(name = "decommendation_count")
	private Long decommendationCount;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "group")
	private Long group;

	@Column(name = "depth")
	private Long depth;

	@Column(name = "group_order")
	private Long groupOrder;

	@Column(name = "child_count")
	private Long childCount;

	@Builder
	public Comment(Comment parentComment, Post post, User user, Status status, String writer, String content,
		Long group, Long depth, Long groupOrder, Long childCount) {
		this.parentComment = parentComment;
		this.post = post;
		this.user = user;
		this.status = status;
		this.writer = writer;
		this.content = content;
		this.recommendationCount = 0L;
		this.decommendationCount = 0L;
		this.createdAt = LocalDateTime.now();
		this.group = group;
		this.depth = depth;
		this.groupOrder = groupOrder;
		this.childCount = childCount;
	}

	public static Comment toEntity(CreateCommentRequest request, Comment parentComment, Post post, User user,
		Status status, Long group, Long depth, Long groupOrder, Long childCount) {
		return Comment.builder()
			.parentComment(parentComment)
			.post(post)
			.user(user)
			.status(status)
			.writer(user.getNickname())
			.content(request.content())
			.group(group)
			.depth(depth)
			.groupOrder(groupOrder)
			.childCount(childCount)
			.build();
	}

	public static Comment toEntity(CreateCommentRequest request, Comment parentComment, Post post, User user,
		Status status, Long group) {
		return Comment.builder()
			.parentComment(parentComment)
			.post(post)
			.user(user)
			.status(status)
			.writer(user.getNickname())
			.content(request.content())
			.group(group)
			.depth(0L)
			.groupOrder(0L)
			.childCount(0L)
			.build();
	}

	public void updateComment(UpdateCommentRequest request) {
		this.content = request.content();
	}

	public void updateStatus(Status status) {
		this.status = status;
	}

	public void setDeletedAt() {
		this.deletedAt = LocalDateTime.now();
	}

	public void recommendComment() {
		this.recommendationCount++;
	}

	public void disrecommendComment() {
		this.decommendationCount++;
	}

	public void addChildCount() {
		this.childCount++;
	}

	public void minusChildCount() {
		this.childCount--;
	}

	public void addGroupOrder() {
		this.groupOrder++;
	}

	public void minusGroupOrder() {
		this.groupOrder--;
	}
}
