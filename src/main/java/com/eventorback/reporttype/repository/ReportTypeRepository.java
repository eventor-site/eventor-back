package com.eventorback.reporttype.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.reporttype.domain.entity.ReportType;

public interface ReportTypeRepository extends JpaRepository<ReportType, Long>, CustomReportTypeRepository {

	boolean existsByName(String name);

	boolean existsByReportTypeIdNotAndName(Long reportTypeId, String name);

	Optional<ReportType> findByName(String name);
}