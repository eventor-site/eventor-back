package com.eventorback.bannickname.repository.impl;

import static com.eventorback.bannickname.domain.entity.QBanNickname.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.bannickname.domain.dto.BanNicknameDto;
import com.eventorback.bannickname.repository.CustomBanNicknameRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomBanNicknameRepositoryImpl implements CustomBanNicknameRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<String> getCacheBanNicknames() {
		return queryFactory
			.select(banNickname.nickname)
			.from(banNickname)
			.orderBy(banNickname.nickname.asc())
			.fetch();
	}

	@Override
	public List<BanNicknameDto> getBanNicknames() {
		return queryFactory
			.select(Projections.constructor(
				BanNicknameDto.class,
				banNickname.banNicknameId,
				banNickname.nickname
			))
			.from(banNickname)
			.orderBy(banNickname.nickname.asc())
			.fetch();
	}

	@Override
	public Page<BanNicknameDto> getBanNicknames(Pageable pageable) {
		List<BanNicknameDto> result = queryFactory
			.select(Projections.constructor(
				BanNicknameDto.class,
				banNickname.banNicknameId,
				banNickname.nickname
			))
			.from(banNickname)
			.orderBy(banNickname.nickname.asc())
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(banNickname.count())
			.from(banNickname)
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}

}
