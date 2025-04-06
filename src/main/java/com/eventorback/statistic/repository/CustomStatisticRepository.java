package com.eventorback.statistic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.statistic.domain.dto.response.GetStatistic;

public interface CustomStatisticRepository {

	Page<GetStatistic> getStatistics(Pageable pageable);
}
