package com.eventorback.point.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.commentrecommend.domain.entity.CommentRecommend;
import com.eventorback.point.domain.entity.Point;
import com.eventorback.point.repository.PointRepository;
import com.eventorback.pointhistory.domain.entity.PointHistory;
import com.eventorback.pointhistory.repository.PointHistoryRepository;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.postrecommend.domain.entity.PostRecommend;
import com.eventorback.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@Order(2)
@Transactional
@RequiredArgsConstructor
public class PointAspect {
	private final UserRepository userRepository;
	private final PointRepository pointRepository;
	private final PointHistoryRepository pointHistoryRepository;

	/**
	 * 글쓰기 포인트 계산
	 */
	@AfterReturning("execution(* com.eventorback.post.repository.PostRepository.save(..))")
	public void pointCalculatorAfterReturningSavePost(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof Post post) {
			Point point = pointRepository.findOrCreatePoint("글쓰기");
			pointHistoryRepository.save(new PointHistory(post.getUser(), point));
			post.getUser().updatePoint(point.getAmount());
		}
	}

	/**
	 * 댓글쓰기 포인트 계산
	 */
	@AfterReturning("execution(* com.eventorback.comment.repository.CommentRepository.save(..))")
	public void pointCalculatorAfterReturningSaveComment(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof Comment comment) {
			Point point = pointRepository.findOrCreatePoint("댓글쓰기");
			pointHistoryRepository.save(new PointHistory(comment.getUser(), point));
			comment.getUser().updatePoint(point.getAmount());
		}
	}

	/**
	 * 게시글 추천, 게시글 추천 받기 포인트 계산
	 */
	@AfterReturning("execution(* com.eventorback.postrecommend.repository.PostRecommendRepository.save(..))")
	public void pointCalculatorAfterReturningPostRecommend(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof PostRecommend postRecommend) {
			Point point;
			if (postRecommend.getRecommendType().getName().equals("추천")) {
				point = pointRepository.findOrCreatePoint("게시글 추천");
				pointHistoryRepository.save(new PointHistory(postRecommend.getUser(), point));
				postRecommend.getUser().updatePoint(point.getAmount());

				point = pointRepository.findOrCreatePoint("게시글 추천받기");
				pointHistoryRepository.save(new PointHistory(postRecommend.getPost().getUser(), point));
				postRecommend.getPost().getUser().updatePoint(point.getAmount());
			} else {
				point = pointRepository.findOrCreatePoint("게시글 비추천받기");
				pointHistoryRepository.save(new PointHistory(postRecommend.getPost().getUser(), point));
				postRecommend.getPost().getUser().updatePoint(point.getAmount());
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
			Point point;
			if (commentRecommend.getRecommendType().getName().equals("추천")) {
				point = pointRepository.findOrCreatePoint("댓글 추천");
				pointHistoryRepository.save(new PointHistory(commentRecommend.getUser(), point));
				commentRecommend.getUser().updatePoint(point.getAmount());

				point = pointRepository.findOrCreatePoint("댓글 추천받기");
				pointHistoryRepository.save(new PointHistory(commentRecommend.getComment().getUser(), point));
				commentRecommend.getComment().getUser().updatePoint(point.getAmount());
			} else {
				point = pointRepository.findOrCreatePoint("댓글 비추천받기");
				pointHistoryRepository.save(new PointHistory(commentRecommend.getComment().getUser(), point));
				commentRecommend.getComment().getUser().updatePoint(point.getAmount());
			}
		}
	}

	/**
	 * 글 삭제 시 포인트 계산
	 */
	@AfterReturning(value = "execution(* com.eventorback.post.service.impl.PostServiceImpl.deletePost(..))", returning = "result")
	public void pointCalculatorAfterReturningDeletePost(Object result) {
		if (result instanceof Post post) {
			Point point = pointRepository.findOrCreatePoint("글삭제");
			pointHistoryRepository.save(new PointHistory(post.getUser(), point));
			post.getUser().updatePoint(point.getAmount());
			userRepository.save(post.getUser());
		}
	}

	/**
	 * 댓글 삭제 시 포인트 계산
	 */
	@AfterReturning(value = "execution(* com.eventorback.comment.service.impl.CommentServiceImpl.deleteComment(..))", returning = "result")
	public void pointCalculatorAfterReturningDeleteComment(Object result) {
		if (result instanceof Comment comment) {
			Point point = pointRepository.findOrCreatePoint("댓글삭제");
			pointHistoryRepository.save(new PointHistory(comment.getUser(), point));
			comment.getUser().updatePoint(point.getAmount());
			userRepository.save(comment.getUser());
		}
	}
}
