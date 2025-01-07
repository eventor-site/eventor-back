package com.eventorback.postview.domain.entity;

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
	private Post post;

	@Builder
	public PostView(User user, Post post) {
		this.user = user;
		this.post = post;
	}

	public static PostView toEntity(User user, Post post) {
		return PostView.builder()
			.user(user)
			.post(post)
			.build();
	}
}
