package com.eventorback.postreport.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.post.domain.entity.Post;
import com.eventorback.post.exception.PostNotFoundException;
import com.eventorback.post.repository.PostRepository;
import com.eventorback.postreport.domain.dto.response.GetPostReportResponse;
import com.eventorback.postreport.domain.entity.PostReport;
import com.eventorback.postreport.repository.PostReportRepository;
import com.eventorback.postreport.service.PostReportService;
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
public class PostReportServiceImpl implements PostReportService {
	private final PostReportRepository postReportRepository;
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final ReportTypeRepository reportTypeRepository;

	@Override
	public List<GetPostReportResponse> getPostReports() {
		return postReportRepository.getPostReports();
	}

	@Override
	public Page<GetPostReportResponse> getPostReports(Pageable pageable) {
		return null;
	}

	@Override
	public String createPostReport(Long userId, Long postId, String reportTypeName) {
		if (userId == null) {
			return "로그인 후 이용해 주세요.";
		} else if (postReportRepository.existsByUserUserIdAndPostPostId(userId, postId)) {
			return "이미 신고한 게시물 입니다.";
		} else {
			User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
			Post post = postRepository.findById(postId)
				.orElseThrow(() -> new PostNotFoundException(postId));
			ReportType reportType = reportTypeRepository.findByName(reportTypeName)
				.orElseThrow(() -> new ReportTypeNotFoundException(reportTypeName));
			postReportRepository.save(PostReport.toEntity(user, post, reportType));
			return "게시물을 신고 하였습니다.";
		}
	}

	@Override
	public String deletePostReport(Long userId, Long postReportId) {
		if (userId == null) {
			return "로그인 후 이용해 주세요.";
		} else {
			postReportRepository.deleteById(postReportId);
			return "게시물 신고가 삭제 되었습니다.";
		}
	}
}
