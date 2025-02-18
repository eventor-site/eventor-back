package com.eventorback.bookmark.domain.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.eventorback.category.domain.entity.Category;
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
@Table(name = "bookmarks")
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
	@OnDelete(action = OnDeleteAction.CASCADE) // DB 에서 자동 삭제 처리
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
