package com.eventorback.grade.aspect;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.commentrecommend.domain.entity.CommentRecommend;
import com.eventorback.grade.domain.entity.Grade;
import com.eventorback.grade.service.GradeService;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.postrecommend.domain.entity.PostRecommend;
import com.eventorback.user.domain.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@Order(1)
@Transactional
@RequiredArgsConstructor
public class GradeAspect {
	private final GradeService gradeService;
	private final ObjectMapper objectMapper;

	/**
	 * 글쓰기 등급 계산
	 */
	@AfterReturning("execution(* com.eventorback.post.repository.PostRepository.save(..))")
	public void pointCalculatorAfterReturningSavePost(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof Post post) {
			updateUserGrade(post.getUser());
		}
	}

	/**
	 * 댓글쓰기 등급 계산
	 */
	@AfterReturning("execution(* com.eventorback.comment.repository.CommentRepository.save(..))")
	public void pointCalculatorAfterReturningSaveComment(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof Comment comment) {
			updateUserGrade(comment.getUser());
		}
	}

	/**
	 * 게시글 추천, 게시글 추천 받기 등급 계산
	 */
	@AfterReturning("execution(* com.eventorback.postrecommend.repository.PostRecommendRepository.save(..))")
	public void pointCalculatorAfterReturningPostRecommend(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof PostRecommend postRecommend) {
			updateUserGrade(postRecommend.getUser());

		}
	}

	/**
	 * 댓글 추천, 댓글 추천 받기 등급 계산
	 */
	@AfterReturning("execution(* com.eventorback.commentrecommend.repository.CommentRecommendRepository.save(..))")
	public void pointCalculatorAfterReturningCommentRecommend(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof CommentRecommend commentRecommend) {
			updateUserGrade(commentRecommend.getUser());
		}
	}

	/**
	 * 댓글 삭제 시 등급 계산
	 */
	@AfterReturning(value = "execution(* com.eventorback.comment.service.impl.CommentServiceImpl.deleteComment(..))", returning = "result")
	public void pointCalculatorAfterReturningDeleteComment(Object result) {
		if (result instanceof Comment comment) {
			updateUserGrade(comment.getUser());
		}
	}

	/**
	 * 등급을 결정하는 메서드
	 */
	public void updateUserGrade(User user) {
		List<?> cachedList = gradeService.findAllByOrderByMinAmountAsc();
		List<Grade> grades = objectMapper.convertValue(cachedList, new TypeReference<List<Grade>>() {
		});

		boolean isAdmin = user.getUserRoles().stream()
			.anyMatch(userRole -> "admin".equals(userRole.getRole().getName()));

		if (isAdmin) {
			return; // 관리자는 등급 업데이트를 하지 않음
		}

		for (Grade grade : grades) {
			if (grade.getMinAmount() <= user.getPoint() && user.getPoint() <= grade.getMaxAmount()) {
				if (!user.getGrade().getName().equals(grade.getName())) {
					user.updateGrade(grade);
				}
				break;
			}
		}
	}

}
