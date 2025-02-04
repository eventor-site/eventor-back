package com.eventorback.commentRecommend.domain.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.recommendtype.domain.entity.RecommendType;
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
public class CommentRecommend {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_recommend_id")
	private Long commentRecommendId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "comment_id")
	@OnDelete(action = OnDeleteAction.CASCADE) // DB 에서 자동 삭제 처리
	private Comment comment;

	@ManyToOne(optional = false)
	@JoinColumn(name = "recommend_type")
	private RecommendType recommendType;

	@Builder
	public CommentRecommend(User user, Comment comment, RecommendType recommendType) {
		this.user = user;
		this.comment = comment;
		this.recommendType = recommendType;
	}

	public static CommentRecommend toEntity(User user, Comment comment, RecommendType recommendType) {
		return CommentRecommend.builder()
			.user(user)
			.comment(comment)
			.recommendType(recommendType)
			.build();
	}
}
