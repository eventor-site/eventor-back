package com.eventorback.integration.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.integration.domain.entity.Integration;

public interface IntegrationRepository extends JpaRepository<Integration, Long> {

	List<Integration> findAllByOrderBySortOrderAscIntegrationIdAsc();

	boolean existsByCode(String code);
}
