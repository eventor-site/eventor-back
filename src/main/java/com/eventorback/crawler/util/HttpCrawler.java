package com.eventorback.crawler.util;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpCrawler {
	private final HttpClient httpClient;
	private final Random random = new Random();
	private static final long REQUEST_DELAY = 5000; // 5초 간격 ( 크롤링 차단 회피 최소값)
	private static final String[] USER_AGENTS = {
		"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
		"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
		"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
		"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0",
		"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.1 Safari/605.1.15"
	};

	public HttpCrawler() {
		// 쿠키 매니저 설정으로 세션 유지
		CookieManager cookieManager = new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		
		this.httpClient = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(30))
			.followRedirects(HttpClient.Redirect.NORMAL)
			.cookieHandler(cookieManager)
			.build();
	}

	public String getPageContent(String url) throws IOException, InterruptedException {
		return getPageContentWithRetry(url, 3);
	}
	
	private String getPageContentWithRetry(String url, int maxRetries) throws IOException, InterruptedException {
		// 랜덤 지연으로 자연스러운 접근 패턴 모방
		long randomDelay = REQUEST_DELAY + random.nextInt(3000); // 5-8초 랜덤 지연
		Thread.sleep(randomDelay);
		
		// 랜덤 User-Agent 선택
		String userAgent = USER_AGENTS[random.nextInt(USER_AGENTS.length)];
		
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
			.uri(URI.create(url))
			.header("User-Agent", userAgent)
			.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
			.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
			.header("Accept-Encoding", "gzip, deflate, br")
			.header("Cache-Control", "no-cache")
			.header("Pragma", "no-cache")
			.header("Sec-Fetch-Dest", "document")
			.header("Sec-Fetch-Mode", "navigate")
			.header("Sec-Fetch-Site", "none")
			.header("Sec-Fetch-User", "?1")
			.header("Upgrade-Insecure-Requests", "1")
			.timeout(Duration.ofSeconds(30));
		
		// Referer 헤더 추가 (에펨코리아 내부 링크인 것처럼 위장)
		if (url.contains("fmkorea.com")) {
			requestBuilder.header("Referer", "https://www.fmkorea.com/hotdeal");
		}

		HttpRequest request = requestBuilder.build();
		
		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			
			log.info("HTTP {} for URL: {}", response.statusCode(), url);
			
			if (response.statusCode() == 200) {
				return response.body();
			} else if (response.statusCode() == 429) {
				// Rate limit 에러 시 더 긴 대기
				log.warn("Rate limited. Waiting 30 seconds before retry...");
				Thread.sleep(30000);
				throw new IOException("Rate limited (HTTP 429) for URL: " + url);
			} else if (response.statusCode() == 403) {
				log.warn("Access forbidden (HTTP 403) for URL: {}", url);
				throw new IOException("Access forbidden (HTTP 403) for URL: " + url);
			} else {
				throw new IOException("HTTP " + response.statusCode() + " for URL: " + url);
			}
		} catch (IOException e) {
			if (maxRetries > 1) {
				log.warn("Request failed, retrying... ({} attempts left)", maxRetries - 1);
				Thread.sleep(10000 + random.nextInt(5000)); // 10-15초 대기 후 재시도
				return getPageContentWithRetry(url, maxRetries - 1);
			} else {
				throw e;
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw e;
		}
	}

	public List<String> extractHotDealLinks(String html, String lastUrl) {
		List<String> results = new ArrayList<>();
		Document doc = Jsoup.parse(html);

		Elements items = doc.select("li.li_best2_pop0 h3.title a.hotdeal_var8");

		for (Element item : items) {
			String href = item.attr("href");
			String fullUrl = href.startsWith("http") ? href : "https://www.fmkorea.com" + href;

			if (fullUrl.equals(lastUrl)) {
				break;
			}

			results.add(fullUrl);
		}

		return results.reversed();
	}

	public String extractTitle(String html) {
		Document doc = Jsoup.parse(html);
		Element titleElement = doc.selectFirst("div.board .top_area h1 span");
		return titleElement != null ? titleElement.text() : "";
	}

	public String extractTextByLabel(String html, String label) {
		Document doc = Jsoup.parse(html);
		Elements rows = doc.select("table.hotdeal_table tr");

		for (Element row : rows) {
			Element th = row.selectFirst("th");
			if (th != null && th.text().trim().equals(label)) {
				Element td = row.selectFirst("td .xe_content");
				return td != null ? td.text().trim() : "";
			}
		}
		return "";
	}

	public String extractAttrByLabel(String html, String label, String attr) {
		Document doc = Jsoup.parse(html);
		Elements rows = doc.select("table.hotdeal_table tr");

		for (Element row : rows) {
			Element th = row.selectFirst("th");
			if (th != null && th.text().trim().equals(label)) {
				Element link = row.selectFirst("td .xe_content a");
				return link != null ? link.attr(attr).trim() : "";
			}
		}
		return "";
	}

	public List<String> extractImageUrls(String html) {
		List<String> imageUrls = new ArrayList<>();
		Document doc = Jsoup.parse(html);

		Element contentTag = doc.selectFirst("div.rd_body .xe_content");
		if (contentTag != null) {
			Elements imgTags = contentTag.select("img");

			for (Element img : imgTags) {
				String src = img.attr("src");
				if (src == null || src.trim().isEmpty()) {
					continue;
				}

				// 상대 URL을 절대 URL로 변환
				if (src.startsWith("//")) {
					src = "https:" + src;
				} else if (src.startsWith("/")) {
					src = "https://www.fmkorea.com" + src;
				}

				// 이미지 URL이 유효한지 확인
				if (src.contains("fmkorea.com") && List.of(".jpg", ".jpeg", ".png", ".gif", ".webp").contains(src)) {
					imageUrls.add(src);
				}
			}
		}

		return imageUrls;
	}

	public String extractContent(String html) {
		Document doc = Jsoup.parse(html);
		Element contentTag = doc.selectFirst("div.rd_body .xe_content");
		return contentTag != null ? contentTag.text() : "";
	}
	
	/**
	 * 세션 초기화를 위해 메인 페이지를 먼저 방문
	 */
	public void initializeSession() {
		try {
			log.info("Initializing session by visiting main page...");
			getPageContent("https://www.fmkorea.com/hotdeal");
			log.info("Session initialized successfully");
		} catch (Exception e) {
			log.warn("Failed to initialize session: {}", e.getMessage());
		}
	}

}