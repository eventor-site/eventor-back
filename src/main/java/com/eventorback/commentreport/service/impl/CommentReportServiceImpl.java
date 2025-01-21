package com.eventorback.commentreport.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
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
	public List<GetCommentReportResponse> getCommentReports() {
		return commentReportRepository.getCommentReports();
	}

	@Override
	public Page<GetCommentReportResponse> getCommentReports(Pageable pageable) {
		return null;
	}

	@Override
	public String createCommentReport(Long userId, Long commentId, String reportTypeName) {
		if (userId == null) {
			return "로그인 후 이용해 주세요.";
		} else if (commentReportRepository.existsByUserUserIdAndCommentCommentId(userId, commentId)) {
			return "이미 신고한 댓글 입니다.";
		} else {
			User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
			Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new CommentNotFoundException(commentId));
			ReportType reportType = reportTypeRepository.findByName(reportTypeName)
				.orElseThrow(() -> new ReportTypeNotFoundException(reportTypeName));
			commentReportRepository.save(CommentReport.toEntity(user, comment, reportType));
			return "댓글을 신고 하였습니다.";
		}
	}

	@Override
	public void confirmCommentReport(Long commentReportId) {
		CommentReport commentReport = commentReportRepository.findById(commentReportId)
			.orElseThrow(() -> new CommentNotFoundException(commentReportId));
		commentReport.confirm();
	}

	@Override
	public String deleteCommentReport(Long userId, Long commentReportId) {
		if (userId == null) {
			return "로그인 후 이용해 주세요.";
		} else {
			commentReportRepository.deleteById(commentReportId);
			return "댓글 신고가 삭제 되었습니다.";
		}
	}
}
