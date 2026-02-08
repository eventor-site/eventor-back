package com.eventorback.crawler.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.eventorback.crawler.domain.dto.response.CrawlFmkoreaDetailResponse;
import com.eventorback.crawler.domain.dto.response.CrawlFmkoreaItemResponse;
import com.eventorback.crawler.domain.entity.Crawler;
import com.eventorback.crawler.respository.CrawlerRepository;
import com.eventorback.crawler.util.AutoCloseableWebDriver;
import com.eventorback.crawler.util.WebDriverFactory;
import com.eventorback.global.annotation.TimedExecution;
import com.eventorback.image.exception.ImageConvertException;
import com.eventorback.image.service.CustomMultipartFile;
import com.eventorback.image.service.ImageService;
import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.service.PostService;
import com.eventorback.user.domain.dto.CurrentUserDto;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CrawlerServiceImpl implements CrawlerService {
	private final CrawlerRepository crawlerRepository;
	private final ImageService imageService;
	private final PostService postService;
	private static final Long ADMIN_USERID = 3L;

	@TimedExecution("에펨코리아 핫딜 게시물 크롤링")
	public void refreshAndSaveNewItems() {
		Crawler crawler = crawlerRepository.findTopByOrderByCrawlerIdDesc()
			.orElseGet(() -> crawlerRepository.save(new Crawler("null")));
		String lastUrl = crawler.getLastUrl();

		List<CrawlFmkoreaItemResponse> latestCrawled = crawlHotDealUntil(lastUrl);

		if (latestCrawled.isEmpty()) {
			return;
		}

		List<CrawlFmkoreaDetailResponse> newItems = latestCrawled.stream()
			.map(item -> parseDetail(item.url()))
			.filter(Objects::nonNull)
			.toList();

		for (CrawlFmkoreaDetailResponse newItem : newItems) {
			CreatePostRequest request = CreatePostRequest.builder()
				.categoryName("핫딜")
				.title(newItem.title())
				.content(newItem.content())
				.link(newItem.link())
				.shoppingMall(newItem.shoppingMall())
				.productName(newItem.productName())
				.price(newItem.price())
				.build();

			Long postId = postService.createPost(new CurrentUserDto(ADMIN_USERID, List.of("admin")), request, false)
				.postId();

			log.info("이미지 다운로드 시작: {} 개", newItem.images().size());
			for (String imageUrl : newItem.images()) {
				try {
					log.info("이미지 다운로드 시도: {}", imageUrl);
					MultipartFile multipartFile = downloadImageAsMultipartFile(imageUrl);
					MultipartFile convertMultipartFile = convertToWebp(multipartFile);
					imageService.upload(convertMultipartFile, "postimage", postId, "핫딜", false, false);
					log.info("이미지 업로드 성공: {}", imageUrl);
				} catch (Exception e) {
					log.warn("이미지 업로드 실패: {} - {}", imageUrl, e.getMessage());
					continue;
				}
			}

			crawler.updateCrawler(newItem.url());
		}
	}

	public List<CrawlFmkoreaItemResponse> crawlHotDealUntil(String lastUrl) {
		List<CrawlFmkoreaItemResponse> results = new ArrayList<>();
		AutoCloseableWebDriver driver = null;

		try {
			driver = WebDriverFactory.createChromeDriver();
			driver.get("https://www.fmkorea.com/hotdeal");
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

			List<WebElement> items = driver.findElements(By.cssSelector("li.li_best2_pop0 h3.title a.hotdeal_var8"));

			for (WebElement aTag : items) {
				String title = aTag.getText().trim();
				String href = aTag.getAttribute("href");
				String fullUrl = href.startsWith("http") ? href : "https://www.fmkorea.com" + href;

				if (fullUrl.equals(lastUrl)) {
					break;
				}

				results.add(new CrawlFmkoreaItemResponse(title, fullUrl));
			}

			return results.reversed();
		} catch (Exception e) {
			log.error("에펨코리아 핫딜 리스트 크롤링 실패: " + e.getMessage());
			return List.of();
		} finally {
			closeDriverSafely(driver);
		}
	}

	public CrawlFmkoreaDetailResponse parseDetail(String url) {
		AutoCloseableWebDriver driver = null;

		try {
			driver = WebDriverFactory.createChromeDriver();
			driver.get(url);
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

			String title = driver.findElement(By.cssSelector("div.board .top_area h1 span")).getText();
			String link = getAttrByLabel(driver, "링크", "href");
			String shoppingMallStr = getTextByLabel(driver, "쇼핑몰");
			String shoppingMall = formattingShoppingMall(shoppingMallStr);
			String product = getTextByLabel(driver, "상품명");
			String priceStr = getTextByLabel(driver, "가격");
			Long price = extractPriceAsLong(priceStr);

			WebElement contentTag = driver.findElement(By.cssSelector("div.rd_body .xe_content"));

			String contentText = contentTag != null ? contentTag.getText() : "";
			String formattedContent = formattingContent(contentText);
			String formattedSource = formattingSource(url);

			List<String> imageUrlList = new ArrayList<>();
			if (contentTag != null) {
				List<WebElement> imgTags = contentTag.findElements(By.tagName("img"));
				log.info("이미지 태그 개수: {}", imgTags.size());

				for (WebElement img : imgTags) {
					String src = img.getAttribute("src");
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
					if (src.contains("fmkorea.com") && (src.contains(".jpg") || src.contains(".png") || src.contains(
						".gif") || src.contains(".webp"))) {
						imageUrlList.add(src);
						log.info("이미지 URL 추가: {}", src);
					}
				}
			}

			log.info("최종 이미지 URL 개수: {}", imageUrlList.size());

			String combinedContent = formattedContent + formattedSource;

			return new CrawlFmkoreaDetailResponse(url, title, link, shoppingMall, product, price, combinedContent,
				imageUrlList);

		} catch (Exception e) {
			log.info("Selenium 크롤링 실패: " + e.getMessage());
			return null;
		} finally {
			closeDriverSafely(driver);
		}
	}

	private void closeDriverSafely(AutoCloseableWebDriver driver) {
		if (driver != null) {
			try {
				driver.close();
			} catch (Exception e) {
				log.warn("WebDriver 종료 시 오류 무시: " + e.getMessage());
			}
		}
	}

	/**
	 * 지정한 레이블(label)에 해당하는 테이블 행에서 값을 추출합니다.
	 * @param driver Selenium WebDriver 인스턴스
	 * @param label 찾고자 하는 테이블 행의 레이블 텍스트
	 * @return 해당하는 값의 텍스트, 없으면 빈 문자열 반환
	 */
	private String getTextByLabel(WebDriver driver, String label) {
		try {
			List<WebElement> rows = driver.findElements(By.cssSelector("table.hotdeal_table tr"));
			for (WebElement row : rows) {
				WebElement th = row.findElement(By.tagName("th"));
				if (th.getText().trim().equals(label)) {
					WebElement td = row.findElement(By.cssSelector("td .xe_content"));
					return td.getText().trim();
				}
			}
		} catch (Exception ignored) {
		}
		return "";
	}

	private String getAttrByLabel(WebDriver driver, String label, String attr) {
		try {
			List<WebElement> rows = driver.findElements(By.cssSelector("table.hotdeal_table tr"));
			for (WebElement row : rows) {
				WebElement th = row.findElement(By.tagName("th"));
				String thText = th.getText().trim();

				// "링크" 레이블 찾기 (버튼 텍스트 제외)
				if (thText.contains(label)) {
					WebElement td = row.findElement(By.tagName("td"));
					WebElement link = td.findElement(By.cssSelector(".xe_content a.hotdeal_url"));
					String href = link.getAttribute(attr).trim();

					// 실제 URL 추출 (link.fmkorea.org에서 리다이렉트되는 실제 URL)
					if (href.contains("link.fmkorea.org/link.php")) {
						return extractRealUrl(href);
					}
					return href;
				}
			}
		} catch (Exception e) {
			log.warn("링크 추출 실패: {}", e.getMessage());
		}
		return "";
	}

	private String extractRealUrl(String fmkoreaLink) {
		try {
			// URL에서 url 파라미터 추출
			String[] parts = fmkoreaLink.split("url=");
			if (parts.length > 1) {
				String encodedUrl = parts[1].split("&")[0]; // 첫 번째 & 전까지
				return java.net.URLDecoder.decode(encodedUrl, "UTF-8");
			}
		} catch (Exception e) {
			log.warn("실제 URL 추출 실패: {}", e.getMessage());
		}
		return fmkoreaLink; // 실패 시 원본 반환
	}

	/**
	 * 쇼핑몰 값 포맷팅
	 * @param shoppingMall "쿠팡 [포텐 터짐 우대 쇼핑몰, 제휴 링크]"
	 * @return "쿠팡"
	 */
	private String formattingShoppingMall(String shoppingMall) {
		if (shoppingMall != null && !shoppingMall.isBlank()) {
			shoppingMall = shoppingMall.split(" ")[0];  // 첫 단어만 추출
		}

		return shoppingMall;
	}

	/**
	 * 작성 내용 값 포맷팅
	 * @param contentText "추천하는 상품입니다."
	 * @return "<p>추천하는 상품입니다.</p>"
	 */
	private String formattingContent(String contentText) {
		return Arrays.stream(contentText.split("\n"))
			.filter(line -> !line.trim().isEmpty())
			.map(line -> "<p>" + line.trim() + "</p>")
			.collect(Collectors.joining());
	}

	/**
	 * 출처 값 포맷팅, `target="_blank"`와 `rel="noopener noreferrer"` 속성을 포함하여 새 탭에서 열리도록 처리합니다
	 * @param url 원본 출처 링크 (http:// 또는 https:// 포함)
	 * @return HTML 형식의 출처 링크 문자열
	 */
	private String formattingSource(String url) {
		return String.format("<a href=\"%s\" target=\"_blank\" rel=\"noopener noreferrer\">%s</a>", url, "[출처]");
	}

	/**
	 * String 형태의 가격 Long으로 파싱
	 * @param priceStr 12,000원
	 * @return 12000
	 */
	private Long extractPriceAsLong(String priceStr) {
		if (priceStr == null || priceStr.isBlank())
			return 0L;

		// "9,900원" → "9900"
		String numeric = priceStr.replaceAll("[^\\d]", "");  // 숫자 이외 제거

		if (numeric.isEmpty())
			return 0L;
		return Long.parseLong(numeric);
	}

	public MultipartFile downloadImageAsMultipartFile(String fileUrl) throws Exception {
		URI uri = URI.create(fileUrl);
		URL url = uri.toURL();
		URLConnection connection = url.openConnection();

		// User-Agent 헤더 추가로 차단 방지
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
		connection.setRequestProperty("Referer", "https://www.fmkorea.com/");
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);

		String contentType = connection.getContentType();
		String extension = imageService.getFileExtension(new File(url.getPath()).getName());
		String newFileName = "image" + extension;

		try (InputStream inputStream = connection.getInputStream()) {
			return new CustomMultipartFile(
				inputStream,
				newFileName,
				contentType
			);
		}
	}

	public MultipartFile convertToWebp(MultipartFile file) {
		try {

			if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
				throw new ImageConvertException();
			}

			String originalFilename = file.getOriginalFilename();
			String contentType = file.getContentType();

			// 동영상 파일은 변환 없이 그대로 반환
			if (contentType != null && contentType.startsWith("video/")) {
				return file;
			}

			if (originalFilename.endsWith(".webp")) {
				return file;
			}

			ImmutableImage image = ImmutableImage.loader().fromStream(file.getInputStream());

			// ✅ WebP 최대 허용 크기 체크
			if (image.awt().getWidth() > 16383 || image.awt().getHeight() > 16383) {
				throw new ImageConvertException("이미지 크기가 너무 큽니다. \n최대 크기: 16383x16383 픽셀");
			}

			// WebP로 변환 (손실 압축)
			File tempFile = new File(file.getOriginalFilename());
			image.output(WebpWriter.DEFAULT, tempFile);

			// 변환된 데이터를 메모리에 저장 후 임시 파일 삭제
			ByteArrayInputStream webpData = new ByteArrayInputStream(Files.readAllBytes(tempFile.toPath()));
			tempFile.delete(); // 변환 후 즉시 삭제

			return new CustomMultipartFile(
				webpData,
				originalFilename.substring(0, originalFilename.lastIndexOf('.')) + ".webp",
				"image/webp"
			);

		} catch (IOException e) {
			throw new ImageConvertException();
		}
	}

	// public WebDriver createWebDriver() {
	// 	WebDriverManager.chromedriver().setup();
	// 	ChromeOptions options = new ChromeOptions();
	// 	options.addArguments("--headless");
	// 	options.addArguments("--disable-gpu");
	// 	options.addArguments("--lang=ko-KR");
	// 	options.addArguments("--window-size=1920,1080");
	// 	options.addArguments("--user-agent=..."); // 필요시 UA 설정
	// 	return new ChromeDriver(options);
	// }

	// public WebDriver createWebDriver() {
	// 	WebDriverManager.chromedriver().setup();
	// 	ChromeOptions options = new ChromeOptions();
	// 	options.addArguments("--headless", "--no-sandbox", "--disable-gpu", "--disable-dev-shm-usage");
	// 	return new ChromeDriver(options);
	// }
}
