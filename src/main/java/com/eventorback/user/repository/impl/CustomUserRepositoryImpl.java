package com.eventorback.user.repository.impl;

import static com.eventorback.grade.domain.entity.QGrade.*;
import static com.eventorback.role.domain.entity.QRole.*;
import static com.eventorback.status.domain.entity.QStatus.*;
import static com.eventorback.user.domain.entity.QUser.*;
import static com.eventorback.userrole.domain.entity.QUserRole.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.user.domain.dto.response.GetUserByIdentifier;
import com.eventorback.user.domain.dto.response.GetUserByUserId;
import com.eventorback.user.domain.dto.response.GetUserListResponse;
import com.eventorback.user.domain.dto.response.GetUserResponse;
import com.eventorback.user.domain.dto.response.GetUserTokenInfo;
import com.eventorback.user.domain.dto.response.OauthDto;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.repository.CustomUserRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<GetUserListResponse> getUsers(Pageable pageable) {

		// 사용자 정보와 역할 목록을 함께 가져오는 쿼리
		List<Tuple> userTuples = queryFactory
			.select(
				userRole.user.userId,
				userRole.user.nickname,
				userRole.user.status.name,
				userRole.user.grade.name,
				userRole.role.name
			)
			.from(userRole)
			// .leftJoin(userRole).on(userRole.user.userId.eq(user.userId)) // user 와 userRole 조인
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		// Tuple 을 GetUserListResponse 로 변환
		Map<Long, GetUserListResponse> userMap = new HashMap<>();
		for (Tuple tuple : userTuples) {
			Long userId = tuple.get(userRole.user.userId);
			String nickname = tuple.get(userRole.user.nickname);
			String statusName = tuple.get(userRole.user.status.name);
			String gradeName = tuple.get(userRole.user.grade.name);
			String roleName = tuple.get(userRole.role.name);

			// 사용자 정보가 이미 Map에 있는지 확인
			GetUserListResponse userResponse = userMap.get(userId);
			if (userResponse == null) {
				// 새로운 사용자 정보 생성
				userResponse = new GetUserListResponse(userId, nickname, statusName, gradeName, new ArrayList<>());
				userMap.put(userId, userResponse);
			}

			// 역할 목록에 역할 추가
			if (roleName != null) {
				userResponse.roles().add(roleName);
			}
		}

		// Map 에서 List 로 변환
		List<GetUserListResponse> result = new ArrayList<>(userMap.values());

		// 전체 사용자 수를 가져오는 쿼리
		Long total = Optional.ofNullable(queryFactory
			.select(user.count())
			.from(user)
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}

	@Override
	public List<GetUserByIdentifier> searchUserByIdentifier(String keyword) {
		return queryFactory
			.select(Projections.constructor(
				GetUserByIdentifier.class,
				user.identifier
			))
			.from(user)
			.where(user.identifier.contains(keyword))
			.fetch();
	}

	@Override
	public List<GetUserByUserId> searchUserByUserId(Long userId) {
		return queryFactory
			.select(Projections.constructor(
				GetUserByUserId.class,
				user.userId,
				user.nickname
			))
			.from(user)
			.where(user.userId.eq(userId))
			.fetch();
	}

	@Override
	public Optional<User> getUser(Long userId) {
		return Optional.ofNullable(queryFactory
			.select(user)
			.from(user)
			.where(user.userId.eq(userId))
			.fetchOne());
	}

	@Override
	public Optional<User> getUser(String identifier) {
		return Optional.ofNullable(queryFactory
			.select(user)
			.from(user)
			.where(user.identifier.eq(identifier))
			.fetchOne());
	}

	@Override
	public GetUserTokenInfo getUserInfoByOauth(OauthDto request) {
		// 사용자 역할 이름 리스트 조회
		List<String> roles = queryFactory
			.select(role.name)
			.from(userRole)
			.join(userRole.user, user)
			.join(userRole.role, role)
			.where(user.oauthId.eq(request.oauthId()), user.oauthType.eq(request.oauthType()))
			.fetch();

		User userInfo = queryFactory
			.selectFrom(user)
			.from(user)
			.where(user.oauthId.eq(request.oauthId()), user.oauthType.eq(request.oauthType()))
			.fetchOne();

		return GetUserTokenInfo.builder()
			.userId(userInfo.getUserId())
			.roles(roles)
			.statusName(userInfo.getStatus().getName())
			.build();
	}

	@Override
	public Optional<GetUserResponse> getUserInfo(Long userId) {
		// 사용자 역할 이름 리스트 조회
		List<String> roles = queryFactory
			.select(role.name)
			.from(userRole)
			.join(userRole.user, user)
			.join(userRole.role, role)
			.where(user.userId.eq(userId))
			.orderBy(userRole.role.name.asc())
			.fetch();

		// 사용자 정보 조회
		User userInfo = queryFactory
			.selectFrom(user)
			.where(user.userId.eq(userId))
			.join(user.status, status).fetchJoin()
			.join(user.grade, grade).fetchJoin()
			.fetchOne();

		// DTO 변환 및 반환
		GetUserResponse response = new GetUserResponse(
			userInfo.getName(),
			userInfo.getNickname(),
			userInfo.getEmail(),
			userInfo.getPhone(),
			userInfo.getBirth(),
			userInfo.getGender(),
			userInfo.getPoint(),
			userInfo.getStatus().getName(),
			userInfo.getGrade().getName(),
			roles,
			userInfo.getOauthType(),
			userInfo.getCreatedAt(),
			userInfo.getUpdatedTime(),
			userInfo.getLastNicknameChangeTime(),
			userInfo.getLastLoginTime()
		);

		return Optional.of(response);
	}

}
