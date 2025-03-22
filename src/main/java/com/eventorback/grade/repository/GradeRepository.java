package com.eventorback.grade.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.grade.domain.entity.Grade;

public interface GradeRepository extends JpaRepository<Grade, Long>, CustomGradeRepository {

	boolean existsByName(String name);

	Optional<Grade> findByName(String name);

	List<Grade> findAllByOrderByMinAmountAsc();
}