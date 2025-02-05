package com.eventorback.postview.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.eventorback.post.domain.entity.Post;
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
public class PostView {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_view_id")
	private Long postViewId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "post_id")
	@OnDelete(action = OnDeleteAction.CASCADE) // DB 에서 자동 삭제 처리
	private Post post;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Builder
	public PostView(User user, Post post) {
		this.user = user;
		this.post = post;
		this.createdAt = LocalDateTime.now();
	}

	public static PostView toEntity(User user, Post post) {
		return PostView.builder()
			.user(user)
			.post(post)
			.build();
	}
}
