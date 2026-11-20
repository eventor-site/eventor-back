package com.eventorback.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.status.domain.entity.Status;
import com.eventorback.user.domain.entity.User;

import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {

	Optional<User> findByUserId(Long userId);

	Optional<User> findByIdentifier(String identifier);

	Optional<User> findByOauthIdAndEmail(String oauthId, String email);

	boolean existsByIdentifier(String identifier);

	boolean existsByNickname(String nickname);

	boolean existsByUserIdNotAndNickname(Long userId, String nickname);

	boolean existsByOauthIdAndOauthType(String oauthId, String oauthType);

	long countByLoginAtGreaterThanEqual(java.time.LocalDateTime loginAt);

	Optional<User> findFirstByOauthTypeAndOauthId(String oauthType, String oauthId);

	/** 구글 로그인 시 같은 이메일의 기존 계정(카카오·네이버·아이디 회원)을 찾는다. 최근 로그인 순 */
	@Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email) AND u.status.name <> '탈퇴' ORDER BY u.loginAt DESC")
	List<User> findLinkCandidatesByEmail(@Param("email") String email);

	@Modifying
	@Transactional
	@Query("UPDATE User u SET u.status = :dormantStatus WHERE u.userId IN :userIds")
	void updateUserStatusToDormant(@Param("userIds") List<Long> userIds, @Param("dormantStatus") Status dormantStatus);

	@Modifying
	@Transactional
	@Query("UPDATE User u SET u.status = :activeStatus WHERE u.userId IN :userIds")
	void updateUserStatusToActive(@Param("userIds") List<Long> userIds, @Param("activeStatus") Status activeStatus);

}