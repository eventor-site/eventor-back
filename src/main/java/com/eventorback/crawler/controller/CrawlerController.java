package com.eventorback.crawler.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.crawler.service.CrawlerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/back/crawler")
@RequiredArgsConstructor
public class CrawlerController {
	private final CrawlerService crawlerService;

	@PostMapping("/hotdeals")
	public ResponseEntity<String> crawlHotDeals() {
		try {
			crawlerService.refreshAndSaveNewItems();
			return ResponseEntity.ok("핫딜 크롤링이 완료되었습니다.");
		} catch (Exception e) {
			return ResponseEntity.internalServerError()
				.body("크롤링 중 오류가 발생했습니다: " + e.getMessage());
		}
	}
}