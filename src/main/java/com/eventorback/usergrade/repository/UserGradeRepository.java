package com.eventorback.usergrade.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.usergrade.domain.entity.UserGrade;

public interface UserGradeRepository extends JpaRepository<UserGrade, Long> {

	boolean existsByName(String name);

	Optional<UserGrade> findByName(String name);
}