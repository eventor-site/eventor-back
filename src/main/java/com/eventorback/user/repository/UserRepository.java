package com.eventorback.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.status.domain.entity.Status;
import com.eventorback.user.domain.entity.User;

import feign.Param;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {

	Optional<User> findByUserId(Long userId);

	Optional<User> findByIdentifier(String identifier);

	boolean existsByIdentifier(String identifier);

	boolean existsByNickname(String nickname);

	boolean existsByUserIdNotAndNickname(Long userId, String nickname);

	boolean existsByOauthIdAndOauthType(String oauthId, String oauthType);

	@Modifying
	@Transactional
	@Query("UPDATE User u SET u.status = :dormantStatus WHERE u.userId IN :userIds")
	void updateUserStatusToDormant(@Param("userIds") List<Long> userIds, @Param("dormantStatus") Status dormantStatus);

	@Modifying
	@Transactional
	@Query("UPDATE User u SET u.status = :activeStatus WHERE u.userId IN :userIds")
	void updateUserStatusToActive(@Param("userIds") List<Long> userIds, @Param("activeStatus") Status activeStatus);

}