package com.eventorback.status.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.status.domain.entity.Status;

public interface StatusRepository extends JpaRepository<Status, Long>, CustomStatusRepository {

	boolean existsByStatusTypeStatusTypeIdAndName(Long statusTypeId, String name);

	boolean existsByStatusIdNotAndNameAndStatusType_StatusTypeId(Long statusId, String name, Long statusTypeId);

	Optional<Status> findByName(String name);
}