package com.eventorback.commentreport.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.comment.exception.CommentNotFoundException;
import com.eventorback.comment.repository.CommentRepository;
import com.eventorback.commentreport.domain.dto.response.GetCommentReportResponse;
import com.eventorback.commentreport.domain.entity.CommentReport;
import com.eventorback.commentreport.repository.CommentReportRepository;
import com.eventorback.commentreport.service.CommentReportService;
import com.eventorback.global.exception.UnauthorizedException;
import com.eventorback.reporttype.domain.entity.ReportType;
import com.eventorback.reporttype.exception.ReportTypeNotFoundException;
import com.eventorback.reporttype.repository.ReportTypeRepository;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.exception.UserNotFoundException;
import com.eventorback.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentReportServiceImpl implements CommentReportService {
	private final CommentReportRepository commentReportRepository;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	private final ReportTypeRepository reportTypeRepository;

	@Override
	public Page<GetCommentReportResponse> getCommentReports(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return commentReportRepository.getCommentReports(PageRequest.of(page, pageSize));
	}

	@Override
	public String createCommentReport(Long userId, Long commentId, String reportTypeName) {
		if (userId == null) {
			return "로그인 후 이용해 주세요.";
		} else if (commentReportRepository.existsByUserUserIdAndCommentCommentId(userId, commentId)) {
			return "이미 신고한 댓글 입니다.";
		} else {
			User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
			Comment comment = commentRepository.findById(commentId)
				.orElseThrow(CommentNotFoundException::new);
			ReportType reportType = reportTypeRepository.findByName(reportTypeName)
				.orElseThrow(ReportTypeNotFoundException::new);
			commentReportRepository.save(CommentReport.toEntity(user, comment, reportType));
			return "댓글을 신고 하였습니다.";
		}
	}

	@Override
	public void confirmCommentReport(Long commentReportId) {
		CommentReport commentReport = commentReportRepository.findById(commentReportId)
			.orElseThrow(CommentNotFoundException::new);
		commentReport.confirm();
	}

	@Override
	public void deleteCommentReport(Long userId, Long commentReportId) {
		if (userId == null) {
			throw new UnauthorizedException();
		}

		commentReportRepository.deleteById(commentReportId);
	}
}
