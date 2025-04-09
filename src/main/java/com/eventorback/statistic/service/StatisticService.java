package com.eventorback.statistic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.statistic.domain.dto.response.GetStatistic;

public interface StatisticService {

	Page<GetStatistic> getStatistics(Pageable pageable);

	void increaseVisitor();
}
