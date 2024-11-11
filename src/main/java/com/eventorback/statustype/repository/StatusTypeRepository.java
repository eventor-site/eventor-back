package com.eventorback.statustype.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sikyeojoback.statustype.domain.entity.StatusType;

public interface StatusTypeRepository extends JpaRepository<StatusType, Long>, CustomStatusTypeRepository {

	boolean existsByName(String name);

	Optional<StatusType> findByName(String name);
}