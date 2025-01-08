package com.eventorback.bookmark.domain.entity;

import com.eventorback.category.domain.entity.Category;
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
public class Bookmark {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bookmark_id")
	private Long bookmarkId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "category_id")
	private Category category;

	@Builder
	public Bookmark(User user, Category category) {
		this.user = user;
		this.category = category;
	}

	public static Bookmark toEntity(User user, Category category) {
		return Bookmark.builder()
			.user(user)
			.category(category)
			.build();
	}
}
