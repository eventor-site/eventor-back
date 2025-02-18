package com.eventorback.postrecommend.domain.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.eventorback.post.domain.entity.Post;
import com.eventorback.recommendtype.domain.entity.RecommendType;
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
@Table(name = "post_recommends")
public class PostRecommend {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_recommend_id")
	private Long postRecommendId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "post_id")
	@OnDelete(action = OnDeleteAction.CASCADE) // DB 에서 자동 삭제 처리
	private Post post;

	@ManyToOne(optional = false)
	@JoinColumn(name = "recommend_type")
	private RecommendType recommendType;

	@Builder
	public PostRecommend(User user, Post post, RecommendType recommendType) {
		this.user = user;
		this.post = post;
		this.recommendType = recommendType;
	}

	public static PostRecommend toEntity(User user, Post post, RecommendType recommendType) {
		return PostRecommend.builder()
			.user(user)
			.post(post)
			.recommendType(recommendType)
			.build();
	}
}
