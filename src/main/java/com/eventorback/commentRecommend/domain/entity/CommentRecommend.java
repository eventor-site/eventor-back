package com.eventorback.commentRecommend.domain.entity;

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
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class CommentRecommend {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_recommend_id")
	private Long commentRecommendId;

	@OneToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "post_id")
	private Post post;

	@ManyToOne(optional = false)
	@JoinColumn(name = "recommend_type")
	private RecommendType recommendType;

	@Builder
	public CommentRecommend(User user, Post post, RecommendType recommendType) {
		this.user = user;
		this.post = post;
		this.recommendType = recommendType;
	}
}
