package com.eventorback.crawler.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "crawlers")
public class Crawler {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crawler_id")
	private Long crawlerId;

	@Column(name = "last_url")
	private String lastUrl;

	@Builder
	public Crawler(String lastUrl) {
		this.lastUrl = lastUrl;
	}

	public void updateCrawler(String lastUrl) {
		this.lastUrl = lastUrl;
	}

}
