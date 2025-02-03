package com.eventorback.status.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.status.domain.entity.Status;

public interface StatusRepository extends JpaRepository<Status, Long>, StatusCustomRepository {

	boolean existsByName(String name);

	Optional<Status> findByName(String name);
}