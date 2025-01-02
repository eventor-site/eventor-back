package com.eventorback.recommendtype.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.recommendtype.domain.entity.RecommendType;

public interface RecommendTypeRepository extends JpaRepository<RecommendType, Long> {

	boolean existsByName(String name);

	Optional<RecommendType> findByName(String name);
}