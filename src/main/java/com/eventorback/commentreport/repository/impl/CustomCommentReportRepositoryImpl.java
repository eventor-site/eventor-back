package com.eventorback.commentreport.repository.impl;

import static com.eventorback.comment.domain.entity.QComment.*;
import static com.eventorback.commentreport.domain.entity.QCommentReport.*;
import static com.eventorback.reporttype.domain.entity.QReportType.*;
import static com.eventorback.status.domain.entity.QStatus.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.util.List;

import com.eventorback.commentreport.domain.dto.response.GetCommentReportResponse;
import com.eventorback.commentreport.repository.CustomCommentReportRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCommentReportRepositoryImpl implements CustomCommentReportRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetCommentReportResponse> getCommentReports() {
		return queryFactory
			.select(Projections.constructor(
				GetCommentReportResponse.class,
				commentReport.commentReportId,
				comment.post.postId,
				comment.commentId,
				user.identifier,
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
			.fetch();
	}

}
