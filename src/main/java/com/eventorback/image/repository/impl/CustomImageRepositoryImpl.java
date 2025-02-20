package com.eventorback.image.repository.impl;

import static com.eventorback.image.domain.entity.QImage.*;

import java.util.List;

import com.eventorback.image.domain.dto.response.GetImageResponse;
import com.eventorback.image.repository.CustomImageRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomImageRepositoryImpl implements CustomImageRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetImageResponse> getAllByPostId(Long postId) {
		return queryFactory
			.select(Projections.constructor(
				GetImageResponse.class,
				image.imageId,
				image.originalName,
				image.url,
				image.size,
				image.isThumbnail))
			.from(image)
			.where(image.post.postId.eq(postId))
			.orderBy(image.imageId.asc())
			.fetch();
	}
}
