package com.eventorback.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.user.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
	Optional<User> findByIdentifier(String identifier);

	boolean existsByIdentifier(String identifier);

	boolean existsByNickname(String nickname);

	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);
}