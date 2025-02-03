package com.eventorback.commentreport.repository.impl;

import static com.eventorback.comment.domain.entity.QComment.*;
import static com.eventorback.commentreport.domain.entity.QCommentReport.*;
import static com.eventorback.reporttype.domain.entity.QReportType.*;
import static com.eventorback.status.domain.entity.QStatus.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.commentreport.domain.dto.response.GetCommentReportResponse;
import com.eventorback.commentreport.repository.CommentReportCustomRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentReportCustomRepositoryImpl implements CommentReportCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<GetCommentReportResponse> getCommentReports(Pageable pageable) {
		List<GetCommentReportResponse> result = queryFactory
			.select(Projections.constructor(
				GetCommentReportResponse.class,
				commentReport.commentReportId,
				comment.post.postId,
				comment.commentId,
				user.userId,
				comment.content,
				commentReport.isChecked,
				commentReport.createdAt,
				reportType.name))
			.from(commentReport)
			.join(commentReport.comment, comment)
			.join(commentReport.user, user)
			.join(commentReport.reportType, reportType)
			.join(comment.status, status)
			.where(status.name.eq("작성됨"))
			.orderBy(commentReport.commentReportId.desc())
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(commentReport.count())
			.from(commentReport)
			.where(commentReport.comment.status.name.eq("작성됨"))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}
}
