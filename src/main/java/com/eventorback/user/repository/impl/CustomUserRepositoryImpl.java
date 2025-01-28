package com.eventorback.user.repository.impl;

import static com.eventorback.grade.domain.entity.QGrade.*;
import static com.eventorback.role.domain.entity.QRole.*;
import static com.eventorback.status.domain.entity.QStatus.*;
import static com.eventorback.user.domain.entity.QUser.*;
import static com.eventorback.userrole.domain.entity.QUserRole.*;

import java.util.List;
import java.util.Optional;

import com.eventorback.user.domain.dto.response.GetUserByIdentifier;
import com.eventorback.user.domain.dto.response.GetUserResponse;
import com.eventorback.user.domain.dto.response.OauthDto;
import com.eventorback.user.domain.dto.response.UserTokenInfo;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.repository.CustomUserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetUserByIdentifier> searchUserByIdentifier(String keyword) {
		return queryFactory
			.select(user.identifier)
			.from(user)
			.where(user.identifier.contains(keyword))
			.fetch()
			.stream()
			.map(GetUserByIdentifier::new)
			.toList();
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
	public UserTokenInfo getUserInfoByOauth(OauthDto request) {
		// 사용자 역할 이름 리스트 조회
		List<String> roles = queryFactory
			.select(role.name)
			.from(userRole)
			.join(userRole.user, user)
			.join(userRole.role, role)
			.where(user.oauthId.eq(request.oauthId()), user.oauthType.eq(request.oauthType()))
			.fetch();

		// 사용자 정보가 없을 경우 null 반환
		if (roles == null) {
			return null;
		}

		User userInfo = queryFactory
			.selectFrom(user)
			.from(user)
			.where(user.oauthId.eq(request.oauthId()), user.oauthType.eq(request.oauthType()))
			.fetchOne();

		return UserTokenInfo.builder()
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
			.fetch();

		// 사용자 정보가 없을 경우 Optional.empty 반환
		if (roles == null) {
			return Optional.empty();
		}

		// 리스트 데이터를 문자열로 변환
		String userRoles = String.join(", ", roles);

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
			userInfo.getStatus().getName(),
			userInfo.getGrade().getName(),
			userRoles,
			userInfo.getOauthType(),
			userInfo.getCreatedAt(),
			userInfo.getUpdatedTime(),
			userInfo.getLastLoginTime()
		);

		return Optional.of(response);
	}

}
