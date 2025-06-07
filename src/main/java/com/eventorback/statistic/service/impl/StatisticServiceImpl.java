package com.eventorback.statistic.service.impl;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.statistic.domain.dto.response.GetStatistic;
import com.eventorback.statistic.domain.entity.Statistic;
import com.eventorback.statistic.repository.StatisticRepository;
import com.eventorback.statistic.service.StatisticService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
	private final StatisticRepository statisticRepository;

	@Override
	public Page<GetStatistic> getStatistics(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return statisticRepository.getStatistics(PageRequest.of(page, pageSize));
	}

	@Override
	public void increaseVisitor() {
		Statistic statistic = statisticRepository.findByDate(LocalDate.now())
			.orElseGet(() -> statisticRepository.save(new Statistic()));

		statistic.increaseVisitorCount();
	}
}
