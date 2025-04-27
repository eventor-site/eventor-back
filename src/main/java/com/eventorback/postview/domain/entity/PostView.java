package com.eventorback.postview.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.eventorback.post.domain.entity.Post;

import jakarta.persistence.CascadeType;
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
@Table(name = "post_views")
public class PostView {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_view_id")
	private Long postViewId;

	@JoinColumn(name = "viewer_id")
	private String viewerId;

	@ManyToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "post_id")
	@OnDelete(action = OnDeleteAction.CASCADE) // DB 에서 자동 삭제 처리
	private Post post;

	@Column(name = "viewed_at")
	private LocalDateTime viewedAt;

	@Builder
	public PostView(String viewerId, Post post) {
		this.viewerId = viewerId;
		this.post = post;
		this.viewedAt = LocalDateTime.now();
	}

	public static PostView toEntity(String viewerId, Post post) {
		return PostView.builder()
			.viewerId(viewerId)
			.post(post)
			.build();
	}

	public void updateViewedAtAt() {
		this.viewedAt = LocalDateTime.now();
	}
}
