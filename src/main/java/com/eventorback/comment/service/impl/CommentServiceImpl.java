package com.eventorback.comment.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.comment.domain.dto.request.CreateCommentRequest;
import com.eventorback.comment.domain.dto.request.UpdateCommentRequest;
import com.eventorback.comment.domain.dto.response.GetCommentByUserIdResponse;
import com.eventorback.comment.domain.dto.response.GetCommentResponse;
import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.comment.domain.entity.CommentClosure;
import com.eventorback.comment.exception.CommentNotFoundException;
import com.eventorback.comment.repository.CommentClosureRepository;
import com.eventorback.comment.repository.CommentRepository;
import com.eventorback.comment.service.CommentService;
import com.eventorback.commentRecommend.domain.entity.CommentRecommend;
import com.eventorback.commentRecommend.repository.CommentRecommendRepository;
import com.eventorback.global.exception.AccessDeniedException;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.post.exception.PostNotFoundException;
import com.eventorback.post.repository.PostRepository;
import com.eventorback.recommendtype.domain.entity.RecommendType;
import com.eventorback.recommendtype.service.RecommendTypeService;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.repository.StatusRepository;
import com.eventorback.user.domain.dto.CurrentUserDto;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.exception.UserNotFoundException;
import com.eventorback.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	private final CommentRepository commentRepository;
	private final CommentClosureRepository commentClosureRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final CommentRecommendRepository commentRecommendRepository;
	private final StatusRepository statusRepository;
	private final RecommendTypeService recommendTypeService;

	@Override
	@Transactional(readOnly = true)
	public List<GetCommentResponse> getCommentsByPostId(CurrentUserDto currentUser, Long postId) {
		return commentRepository.getCommentsByPostId(currentUser, postId);
		// return commentRepository.findAllByPostPostId(postId)
		// 	.stream()
		// 	.map(comment -> GetCommentResponse.fromEntity(comment, currentUser))
		// 	.toList();
	}

	@Override
	public List<GetCommentByUserIdResponse> getComments() {
		return commentRepository.getComments();
	}

	@Override
	@Transactional(readOnly = true)
	public List<GetCommentByUserIdResponse> getCommentsByUserId(Long userId) {
		return commentRepository.getCommentsByUserId(userId);
	}

	@Override
	public void createComment(CreateCommentRequest request, Long postId, Long userId) {
		Post post = postRepository.getPost(postId).orElseThrow(() -> new PostNotFoundException(postId));

		User user = null;
		if (userId != null) {
			user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
			if (user.getStatus().getName().equals("정지")) {
				throw new AccessDeniedException();
			}
		}

		Comment parentComment = null;
		if (request.parentCommentId() != null) {
			parentComment = commentRepository.findById(request.parentCommentId())
				.orElseThrow(() -> new PostNotFoundException(request.parentCommentId()));
		}
		Status status = statusRepository.findOrCreateStatus("댓글", "작성됨");

		Comment newComment = commentRepository.save(Comment.toEntity(request, parentComment, post, user, status));

		// 클로저 테이블에 관계 추가
		createClosureRelations(newComment, parentComment);
	}

	@Override
	public void createClosureRelations(Comment newComment, Comment parentComment) {
		// 1. 부모 댓글의 모든 조상 관계 가져오기
		List<CommentClosure> parentAncestors = commentClosureRepository.findByDescendant(parentComment);

		// 2. 부모 댓글의 조상 → 새 자식 관계 생성
		for (CommentClosure parentAncestor : parentAncestors) {
			CommentClosure closure = CommentClosure.builder()
				.ancestor(parentAncestor.getAncestor()) // 부모의 조상
				.descendant(newComment) // 새 자식
				.depth(parentAncestor.getDepth() + 1) // 깊이 증가
				.build();
			commentClosureRepository.save(closure);
		}

		// 3. 부모 자신 → 새 자식 관계 생성
		CommentClosure parentToChild = CommentClosure.builder()
			.ancestor(parentComment) // 부모 자신
			.descendant(newComment) // 새 자식
			.depth(1L) // 부모에서 바로 연결된 깊이
			.build();
		commentClosureRepository.save(parentToChild);

		// 4. 새 자식 → 자기 자신 관계 생성
		CommentClosure selfRelation = CommentClosure.builder()
			.ancestor(newComment)
			.descendant(newComment)
			.depth(0L) // 자기 자신
			.build();
		commentClosureRepository.save(selfRelation);
	}

	@Override
	public void updateComment(CurrentUserDto currentUser, Long commentId, UpdateCommentRequest request) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentNotFoundException(commentId));

		if (currentUser != null && (!comment.getUser().getUserId().equals(currentUser.userId())) || !currentUser.roles()
			.contains("admin")) {
			throw new AccessDeniedException();
		}

		comment.updateComment(request);
	}

	@Override
	public String recommendComment(Long userId, Long commentId) {
		CommentRecommend commentRecommend = commentRecommendRepository.findByUserUserIdAndCommentCommentId(userId,
			commentId).orElse(null);

		if (commentRecommend == null) {
			User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
			Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new CommentNotFoundException(commentId));
			RecommendType recommendType = recommendTypeService.findOrCreateRecommendType("추천");
			commentRecommendRepository.save(CommentRecommend.toEntity(user, comment, recommendType));
			comment.recommendComment();
			return "추천되었습니다.";
		} else {
			String recommendTypeName = commentRecommend.getRecommendType().getName();
			return "이미 " + recommendTypeName + "하였습니다.";
		}
	}

	@Override
	public String disrecommendComment(Long userId, Long commentId) {
		CommentRecommend commentRecommend = commentRecommendRepository.findByUserUserIdAndCommentCommentId(userId,
			commentId).orElse(null);

		if (commentRecommend == null) {
			User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
			Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new CommentNotFoundException(commentId));
			RecommendType recommendType = recommendTypeService.findOrCreateRecommendType("비추천");
			commentRecommendRepository.save(CommentRecommend.toEntity(user, comment, recommendType));
			comment.disrecommendComment();
			return "비추천되었습니다.";
		} else {
			String recommendTypeName = commentRecommend.getRecommendType().getName();
			return "이미 " + recommendTypeName + "하였습니다.";
		}
	}

	@Override
	public void deleteComment(Long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentNotFoundException(commentId));

		Status status = statusRepository.findOrCreateStatus("댓글", "삭제됨");

		comment.updatePostStatus(status);
	}

}
