package com.eventorback.auth.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.eventorback.auth.domain.RefreshToken;

import jakarta.persistence.LockModeType;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	/** 동시에 같은 토큰으로 재발급하는 요청을 줄 세운다(SELECT ... FOR UPDATE) */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<RefreshToken> findByTokenHash(String tokenHash);

	@Modifying
	@Query("delete from RefreshToken t where t.tokenHash = :tokenHash")
	int deleteByTokenHash(String tokenHash);

	@Modifying
	@Query("delete from RefreshToken t where t.userId = :userId")
	int deleteAllByUserId(Long userId);

	@Modifying
	@Query("delete from RefreshToken t where t.expiresAt < :now")
	int deleteExpired(LocalDateTime now);
}
