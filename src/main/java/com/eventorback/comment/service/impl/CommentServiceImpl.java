package com.eventorback.comment.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.comment.domain.dto.request.CreateCommentRequest;
import com.eventorback.comment.domain.dto.request.UpdateCommentRequest;
import com.eventorback.comment.domain.dto.response.GetCommentResponse;
import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.comment.exception.CommentNotFoundException;
import com.eventorback.comment.repository.CommentRepository;
import com.eventorback.comment.service.CommentService;
import com.eventorback.commentRecommend.domain.entity.CommentRecommend;
import com.eventorback.commentRecommend.repository.CommentRecommendRepository;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.post.exception.PostNotFoundException;
import com.eventorback.post.repository.PostRepository;
import com.eventorback.recommendtype.domain.entity.RecommendType;
import com.eventorback.recommendtype.service.RecommendTypeService;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.service.StatusService;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.exception.UserNotFoundException;
import com.eventorback.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final StatusService statusService;
	private final CommentRecommendRepository commentRecommendRepository;
	private final RecommendTypeService recommendTypeService;

	@Override
	@Transactional(readOnly = true)
	public List<GetCommentResponse> getCommentsByPostId(Long postId) {
		return commentRepository.findAllByPostPostId(postId).stream().map(GetCommentResponse::fromEntity).toList();
	}

	@Override
	public void createComment(CreateCommentRequest request, Long postId, Long userId) {
		Post post = postRepository.getPost(postId).orElseThrow(() -> new PostNotFoundException(postId));
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
		Comment parentComment = null;
		if (request.parentCommentId() != null) {
			parentComment = commentRepository.findById(request.parentCommentId())
				.orElseThrow(() -> new PostNotFoundException(request.parentCommentId()));
		}
		Status status = statusService.findOrCreateStatus("댓글", "댓글 작성됨");

		commentRepository.save(Comment.toEntity(request, parentComment, post, user, status));
	}

	@Override
	public void updateComment(Long commentId, UpdateCommentRequest request) {

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
		commentRepository.deleteById(commentId);
	}
}
