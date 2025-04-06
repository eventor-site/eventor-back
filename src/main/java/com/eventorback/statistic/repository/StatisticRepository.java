package com.eventorback.statistic.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.statistic.domain.entity.Statistic;

public interface StatisticRepository extends JpaRepository<Statistic, Long>, CustomStatisticRepository {

	Optional<Statistic> findByDate(LocalDate localDate);
}