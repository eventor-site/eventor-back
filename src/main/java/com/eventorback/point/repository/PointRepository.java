package com.eventorback.point.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.point.domain.entity.Point;

public interface PointRepository extends JpaRepository<Point, Long>, CustomPointRepository {

	boolean existsByName(String name);

	Optional<Point> findByName(String name);
}