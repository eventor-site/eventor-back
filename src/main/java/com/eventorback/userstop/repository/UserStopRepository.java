package com.eventorback.userstop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.userstop.domain.entity.UserStop;

public interface UserStopRepository extends JpaRepository<UserStop, Long>, CustomUserStopRepository {
	List<UserStop> findAllByUserUserId(Long userId);
}