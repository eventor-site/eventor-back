package com.eventorback.crawler.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.crawler.domain.entity.Crawler;

public interface CrawlerRepository extends JpaRepository<Crawler, Long> {

	Optional<Crawler> findTopByOrderByCrawlerIdDesc();
}
