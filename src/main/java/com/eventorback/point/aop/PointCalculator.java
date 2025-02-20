package com.eventorback.point.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.commentrecommend.domain.entity.CommentRecommend;
import com.eventorback.point.repository.PointRepository;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.postrecommend.domain.entity.PostRecommend;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class PointCalculator {
	private final PointRepository pointRepository;

	/**
	 * 글쓰기 포인트 계산
	 */
	@AfterReturning("execution(* com.eventorback.post.repository.PostRepository.save(..))")
	public void pointCalculatorAfterReturningSavePost(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof Post post) {
			post.getUser().updatePoint(pointRepository.findOrCreatePoint("글쓰기").getAmount());
		}
	}

	/**
	 * 댓글쓰기 포인트 계산
	 */
	@AfterReturning("execution(* com.eventorback.comment.repository.CommentRepository.save(..))")
	public void pointCalculatorAfterReturningSaveComment(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof Comment comment) {
			comment.getUser().updatePoint(pointRepository.findOrCreatePoint("댓글쓰기").getAmount());
		}
	}

	/**
	 * 게시글 추천, 게시글 추천 받기 포인트 계산
	 */
	@AfterReturning("execution(* com.eventorback.postrecommend.repository.PostRecommendRepository.save(..))")
	public void pointCalculatorAfterReturningPostRecommend(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof PostRecommend postRecommend) {
			if (postRecommend.getRecommendType().getName().equals("추천")) {
				postRecommend.getUser().updatePoint(pointRepository.findOrCreatePoint("게시글 추천").getAmount());
				postRecommend.getPost()
					.getUser()
					.updatePoint(pointRepository.findOrCreatePoint("게시글 추천받기").getAmount());
			} else {
				postRecommend.getPost()
					.getUser().updatePoint(pointRepository.findOrCreatePoint("게시글 비추천받기").getAmount());
			}

		}
	}

	/**
	 * 댓글 추천, 댓글 추천 받기 포인트 계산
	 */
	@AfterReturning("execution(* com.eventorback.commentrecommend.repository.CommentRecommendRepository.save(..))")
	public void pointCalculatorAfterReturningCommentRecommend(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof CommentRecommend commentRecommend) {
			if (commentRecommend.getRecommendType().getName().equals("추천")) {
				commentRecommend.getUser().updatePoint(pointRepository.findOrCreatePoint("댓글 추천").getAmount());
				commentRecommend.getComment()
					.getUser()
					.updatePoint(pointRepository.findOrCreatePoint("댓글 추천받기").getAmount());
			} else {
				commentRecommend.getComment()
					.getUser().updatePoint(pointRepository.findOrCreatePoint("댓글 비추천받기").getAmount());
			}

		}
	}

	// 댓글 삭제 시 포인트 계산은 댓글 삭제 비즈니스 로직에 추가
}
