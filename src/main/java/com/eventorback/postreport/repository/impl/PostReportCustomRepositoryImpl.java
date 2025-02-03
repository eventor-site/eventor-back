package com.eventorback.postreport.repository.impl;

import static com.eventorback.post.domain.entity.QPost.*;
import static com.eventorback.postreport.domain.entity.QPostReport.*;
import static com.eventorback.reporttype.domain.entity.QReportType.*;
import static com.eventorback.status.domain.entity.QStatus.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.postreport.domain.dto.response.GetPostReportResponse;
import com.eventorback.postreport.repository.PostReportCustomRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostReportCustomRepositoryImpl implements PostReportCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<GetPostReportResponse> getPostReports(Pageable pageable) {
		List<GetPostReportResponse> result = queryFactory
			.select(Projections.constructor(
				GetPostReportResponse.class,
				postReport.postReportId,
				post.postId,
				user.identifier,
				post.title,
				postReport.isChecked,
				postReport.createdAt,
				reportType.name))
			.from(postReport)
			.join(postReport.post, post)
			.join(postReport.user, user)
			.join(postReport.reportType, reportType)
			.join(post.status, status)
			.where(status.name.eq("작성됨"))
			.orderBy(postReport.postReportId.desc())
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(postReport.count())
			.from(postReport)
			.where(postReport.post.status.name.eq("작성됨"))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}
}
