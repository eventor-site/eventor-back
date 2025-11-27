package com.eventorback.crawler.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpCrawler {
	private final HttpClient httpClient;
	private long lastRequestTime = 0;
	private static final long REQUEST_DELAY = 3000; // 3초 간격으로 증가
	private String sessionCookies = "";

	public HttpCrawler() {
		this.httpClient = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(15))
			.followRedirects(HttpClient.Redirect.NORMAL)
			.build();
	}

	public String getPageContent(String url) throws IOException, InterruptedException {
		// 요청 간격 제어
		long currentTime = System.currentTimeMillis();
		long timeSinceLastRequest = currentTime - lastRequestTime;
		if (timeSinceLastRequest < REQUEST_DELAY) {
			Thread.sleep(REQUEST_DELAY - timeSinceLastRequest);
		}

		int maxRetries = 5;
		for (int attempt = 1; attempt <= maxRetries; attempt++) {
			try {
				HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
					.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
					.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
					.header("Accept-Encoding", "gzip, deflate, br")
					.header("Cache-Control", "no-cache")
					.header("Pragma", "no-cache")
					.header("Sec-Fetch-Dest", "document")
					.header("Sec-Fetch-Mode", "navigate")
					.header("Sec-Fetch-Site", "same-origin")
					.header("Sec-Fetch-User", "?1")
					.header("Upgrade-Insecure-Requests", "1")
					.timeout(Duration.ofSeconds(20));

				// 쿠키 추가
				if (!sessionCookies.isEmpty()) {
					requestBuilder.header("Cookie", sessionCookies);
				}

				// Referer 설정
				if (!url.contains("/hotdeal")) {
					requestBuilder.header("Referer", "https://www.fmkorea.com/hotdeal");
				}

				HttpRequest request = requestBuilder.build();

				lastRequestTime = System.currentTimeMillis();
				HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

				// 쿠키 저장
				response.headers().allValues("set-cookie").forEach(cookie -> {
					if (!sessionCookies.contains(cookie.split(";")[0])) {
						sessionCookies += cookie.split(";")[0] + "; ";
					}
				});

				if (response.statusCode() == 200) {
					return response.body();
				} else if ((response.statusCode() == 430 || response.statusCode() == 429) && attempt < maxRetries) {
					long waitTime = (long) Math.pow(2, attempt) * 3000; // 지수적 백오프
					log.warn("요청 제한 발생, {}ms 대기 후 재시도 ({}/{}): {}",
						waitTime, attempt, maxRetries, url);
					Thread.sleep(waitTime);
					continue;
				} else {
					throw new IOException("HTTP " + response.statusCode() + " for URL: " + url);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw e;
			}
		}

		throw new IOException("최대 재시도 횟수 초과: " + url);
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
				if (src.contains("fmkorea.com") && (src.contains(".jpg") || src.contains(".png") || src.contains(".gif")
					|| src.contains(".webp"))) {
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
}