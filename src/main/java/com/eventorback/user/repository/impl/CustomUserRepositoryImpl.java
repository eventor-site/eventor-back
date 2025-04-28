package com.eventorback.user.repository.impl;

import static com.eventorback.grade.domain.entity.QGrade.*;
import static com.eventorback.role.domain.entity.QRole.*;
import static com.eventorback.status.domain.entity.QStatus.*;
import static com.eventorback.statustype.domain.entity.QStatusType.*;
import static com.eventorback.user.domain.entity.QUser.*;
import static com.eventorback.userrole.domain.entity.QUserRole.*;
import static com.eventorback.userstop.domain.entity.QUserStop.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.user.domain.dto.response.GetUserAuth;
import com.eventorback.user.domain.dto.response.GetUserByIdentifier;
import com.eventorback.user.domain.dto.response.GetUserByUserId;
import com.eventorback.user.domain.dto.response.GetUserListResponse;
import com.eventorback.user.domain.dto.response.GetUserOauth;
import com.eventorback.user.domain.dto.response.GetUserResponse;
import com.eventorback.user.domain.dto.response.OauthDto;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.repository.CustomUserRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<GetUserListResponse> getUsers(Pageable pageable) {
		List<User> users = queryFactory
			.selectFrom(user)
			.leftJoin(user.status, status).fetchJoin()
			.leftJoin(status.statusType, statusType).fetchJoin()
			.leftJoin(user.grade, grade).fetchJoin()
			.leftJoin(user.userRoles, userRole).fetchJoin()        // fetch join 을 사용하여 N+1 문제 방지
			.leftJoin(userRole.role, role)
			.fetchJoin()
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.orderBy(user.createdAt.desc())
			.fetch();

		List<GetUserListResponse> result = users.stream().map(GetUserListResponse::fromEntity).toList();

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
	public GetUserAuth getAuthByIdentifier(String identifier) {
		User result = queryFactory
			.selectFrom(user)
			.from(user)
			.where(user.identifier.eq(identifier))
			.fetchOne();

		if (result != null) {
			return GetUserAuth.fromEntity(result);
		} else {
			return null;
		}
	}

	@Override
	public GetUserOauth getOAuthInfoByOauth(OauthDto request) {
		User result = queryFactory
			.selectFrom(user)
			.from(user)
			.where(user.oauthId.eq(request.oauthId()), user.oauthType.eq(request.oauthType()))
			.fetchOne();

		if (result != null) {
			return GetUserOauth.fromEntity(result);
		} else {
			return null;
		}

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

	@Override
	public List<Long> getDormantUsers() {
		LocalDateTime now = LocalDateTime.now();
		return queryFactory
			.select(user.userId)
			.from(user)
			.join(user.status, status)
			.join(status.statusType, statusType)
			.where(
				status.name.eq("활성")
					// 조건 1: 마지막 로그인 시간이 null 이고, 생성일이 90일 이전인 경우
					.and(user.lastLoginTime.isNull().and(user.createdAt.loe(now.minusDays(90)))
						// 조건 2: 마지막 로그인 시간이 90일 이전이고, 상태가 "활성"인 경우
						.or(user.lastLoginTime.isNotNull().and(user.lastLoginTime.loe(now.minusDays(90))))
					)

			)
			.fetch();
	}

	@Override
	public List<Long> getStopUsers() {
		LocalDateTime now = LocalDateTime.now();

		return queryFactory
			.select(user.userId)
			.from(userStop)
			.join(userStop.user, user)
			.join(user.status, status)
			.join(status.statusType, statusType)
			.where(status.name.eq("정지")
				.and(userStop.endTime.loe(now))
			)
			.fetch();
	}

	@Override
	public List<Long> getNotAdminUsers() {
		return queryFactory
			.select(user.userId)
			.from(userRole)
			.join(userRole.user, user)
			.join(userRole.role, role)
			.where(role.name.ne("admin"))
			.fetch();
	}

	@Override
	public List<User> getExpiredUsers() {
		return queryFactory
			.selectFrom(user)
			.join(user.status, status).fetchJoin()
			.join(status.statusType, statusType).fetchJoin()
			.join(user.grade, grade).fetchJoin()
			.join(user.userRoles, userRole).fetchJoin()        // fetch join 을 사용하여 N+1 문제 방지
			.join(userRole.role, role).fetchJoin()
			.where(status.name.eq("탈퇴")
				.and(user.deletedAt.isNull())
				.and(user.updatedTime.before(LocalDateTime.now().minusDays(90))))
			.fetch();
	}

}
