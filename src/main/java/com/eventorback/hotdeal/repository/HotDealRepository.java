package com.eventorback.hotdeal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.hotdeal.domain.entity.HotDeal;

public interface HotDealRepository extends JpaRepository<HotDeal, Long> {
}