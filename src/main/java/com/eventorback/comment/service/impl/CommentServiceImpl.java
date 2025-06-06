package com.eventorback.comment.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.comment.domain.dto.request.CreateCommentRequest;
import com.eventorback.comment.domain.dto.request.UpdateCommentRequest;
import com.eventorback.comment.domain.dto.response.GetCommentByUserIdResponse;
import com.eventorback.comment.domain.dto.response.GetCommentPageResponse;
import com.eventorback.comment.domain.dto.response.GetCommentResponse;
import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.comment.exception.CommentNotFoundException;
import com.eventorback.comment.repository.CommentRepository;
import com.eventorback.comment.service.CommentService;
import com.eventorback.commentrecommend.domain.entity.CommentRecommend;
import com.eventorback.commentrecommend.repository.CommentRecommendRepository;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.post.exception.PostNotFoundException;
import com.eventorback.post.repository.PostRepository;
import com.eventorback.recommendtype.domain.entity.RecommendType;
import com.eventorback.recommendtype.service.RecommendTypeService;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.repository.StatusRepository;
import com.eventorback.user.domain.dto.CurrentUserDto;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.exception.UserForbiddenException;
import com.eventorback.user.exception.UserNotFoundException;
import com.eventorback.user.repository.UserRepository;
import com.eventorback.userrole.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final CommentRecommendRepository commentRecommendRepository;
	private final StatusRepository statusRepository;
	private final RecommendTypeService recommendTypeService;
	private final UserRoleRepository userRoleRepository;

	@Override
	@Transactional(readOnly = true)
	public Page<GetCommentResponse> getCommentsByPostId(Pageable pageable, CurrentUserDto currentUser, Long postId) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return commentRepository.getCommentsByPostId(PageRequest.of(page, pageSize), currentUser, postId);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<GetCommentByUserIdResponse> getComments(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return commentRepository.getComments(PageRequest.of(page, pageSize));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<GetCommentByUserIdResponse> getCommentsByUserId(Pageable pageable, Long userId) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return commentRepository.getCommentsByUserId(PageRequest.of(page, pageSize), userId);
	}

	@Override
	public GetCommentPageResponse getComment(Long postId, Long commentId) {
		return commentRepository.getComment(postId, commentId);
	}

	@Override
	public void createComment(CurrentUserDto currentUser, CreateCommentRequest request, Long postId) {
		Post post = postRepository.getPost(postId).orElseThrow(PostNotFoundException::new);
		User user = userRepository.findById(currentUser.userId()).orElseThrow(UserNotFoundException::new);
		boolean isAdmin = currentUser.roles().contains("admin");
		Status status = statusRepository.findOrCreateStatus("댓글", "작성됨");

		Comment parentComment = null;
		if (request.parentCommentId() == null) {
			commentRepository.save(
				Comment.toEntity(request, parentComment, post, user, status, commentRepository.getMaxGroup() + 1,
					isAdmin));
		} else {
			parentComment = commentRepository.findById(request.parentCommentId())
				.orElseThrow(CommentNotFoundException::new);

			parentComment.addChildCount();

			if (parentComment.getParentComment() == null) {
				commentRepository.save(
					Comment.toEntity(request, parentComment, post, user, status, parentComment.getGroup(),
						parentComment.getDepth() + 1, commentRepository.getTotalChildCount(parentComment.getGroup()),
						0L, isAdmin));
			} else {
				List<Comment> updateList = commentRepository.getGreaterGroupOrder(parentComment.getGroup(),
					parentComment.getGroupOrder() + parentComment.getChildCount());

				for (Comment comment : updateList) {
					comment.addGroupOrder();
				}
				commentRepository.save(
					Comment.toEntity(request, parentComment, post, user, status, parentComment.getGroup(),
						parentComment.getDepth() + 1, parentComment.getGroupOrder() + parentComment.getChildCount(),
						0L, isAdmin));

			}
		}
	}

	@Override
	public void updateComment(CurrentUserDto currentUser, Long commentId, UpdateCommentRequest request) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(CommentNotFoundException::new);

		if (!comment.getUser().getUserId().equals(currentUser.userId()) && !currentUser.roles().contains("admin")) {
			throw new UserForbiddenException();
		}

		comment.updateComment(request);
	}

	@Override
	public String recommendComment(Long userId, Long commentId) {
		CommentRecommend commentRecommend = commentRecommendRepository.findByUserUserIdAndCommentCommentId(userId,
			commentId).orElse(null);
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(CommentNotFoundException::new);

		if (commentRecommend == null && comment.getUser().getUserId().equals(userId)) {
			return "추천할 수 없습니다.";
		} else if (commentRecommend == null) {
			User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

			RecommendType recommendType = recommendTypeService.findOrCreateRecommendType("추천");
			commentRecommendRepository.save(CommentRecommend.toEntity(user, comment, recommendType));
			comment.recommendComment();
			return "추천되었습니다. 내포인트 +1, 상대포인트 +15";
		} else {
			String recommendTypeName = commentRecommend.getRecommendType().getName();
			return "이미 " + recommendTypeName + "하였습니다.";
		}
	}

	@Override
	public String disrecommendComment(Long userId, Long commentId) {
		CommentRecommend commentRecommend = commentRecommendRepository.findByUserUserIdAndCommentCommentId(userId,
			commentId).orElse(null);

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(CommentNotFoundException::new);

		if (commentRecommend == null && comment.getUser().getUserId().equals(userId)) {
			return "추천할 수 없습니다.";
		} else if (commentRecommend == null) {
			User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

			RecommendType recommendType = recommendTypeService.findOrCreateRecommendType("비추천");
			commentRecommendRepository.save(CommentRecommend.toEntity(user, comment, recommendType));
			comment.disrecommendComment();
			return "비추천되었습니다. 상대포인트 -5";
		} else {
			String recommendTypeName = commentRecommend.getRecommendType().getName();
			return "이미 " + recommendTypeName + "하였습니다.";
		}
	}

	@Override
	public Comment deleteComment(CurrentUserDto currentUser, Long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(CommentNotFoundException::new);
		Status status = statusRepository.findOrCreateStatus("댓글", "삭제됨");

		if (!comment.getUser().getUserId().equals(currentUser.userId()) && !currentUser.roles().contains("admin")) {
			throw new UserForbiddenException();
		}

		comment.setDeletedAt();
		comment.updateStatus(status);

		return comment;
	}

}
