package com.eventorback.auth.repository;

import org.springframework.data.repository.CrudRepository;

import com.eventorback.auth.dto.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

	void deleteByUserId(Long userId);
}
