package com.eventorback.postreport.repository.impl;

import static com.eventorback.post.domain.entity.QPost.*;
import static com.eventorback.postreport.domain.entity.QPostReport.*;
import static com.eventorback.reporttype.domain.entity.QReportType.*;
import static com.eventorback.status.domain.entity.QStatus.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.util.List;

import com.eventorback.postreport.domain.dto.response.GetPostReportResponse;
import com.eventorback.postreport.repository.CustomPostReportRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomPostReportRepositoryImpl implements CustomPostReportRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetPostReportResponse> getPostReports() {
		return queryFactory
			.select(Projections.constructor(
				GetPostReportResponse.class,
				postReport.postReportId,
				post.postId,
				post.writer,
				post.title,
				postReport.createdAt,
				reportType.name))
			.from(postReport)
			.join(postReport.post, post)
			.join(postReport.user, user)
			.join(postReport.reportType, reportType)
			.join(post.status, status)
			.where(status.name.eq("작성됨"))
			.orderBy(postReport.postReportId.desc())
			.fetch();
	}

}
