package com.eventorback.pointhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.pointhistory.domain.entity.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long>, CustomPointHistoryRepository {
}