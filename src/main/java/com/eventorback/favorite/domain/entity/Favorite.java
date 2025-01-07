package com.eventorback.favorite.domain.entity;

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
public class Favorite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "favorite_id")
	private Long favoriteId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "post_id")
	private Post post;

	@Builder
	public Favorite(User user, Post post) {
		this.user = user;
		this.post = post;
	}

	public static Favorite toEntity(User user, Post post) {
		return Favorite.builder()
			.user(user)
			.post(post)
			.build();
	}
}
