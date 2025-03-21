package com.eventorback.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.user.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {

	Optional<User> findByUserId(Long userId);

	Optional<User> findByIdentifier(String identifier);

	boolean existsByIdentifier(String identifier);

	boolean existsByNickname(String nickname);

	boolean existsByUserIdNotAndNickname(Long userId, String nickname);

	boolean existsByEmail(String email);

	boolean existsByOauthIdAndOauthType(String oauthId, String oauthType);

	Optional<List<User>> findByEmail(String email);
}