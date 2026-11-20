package com.eventorback.seo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriUtils;

import com.eventorback.global.util.HtmlSanitizer;

import lombok.RequiredArgsConstructor;

/**
 * 검색엔진용 페이지(docs/migration.md 1절 "SEO").
 * <p>
 * 운영 nginx 는 /posts/{id}, /posts?categoryName=…, /sitemap*.xml 을 이 컨트롤러로 보낸다.
 * React index.html 에 제목·설명·OG 태그·canonical·구조화 데이터와 본문을 미리 넣어 돌려주고,
 * 브라우저에서는 React 가 같은 화면을 다시 그린다(검색엔진은 JS 없이도 내용을 읽는다).
 * 없는 글·삭제된 글은 404 상태로 응답한다.
 */
@RestController
@RequiredArgsConstructor
public class SeoController {
	static final int SITEMAP_PAGE_SIZE = 40_000; // 사이트맵 한 파일 최대 5만 개 제한 안쪽
	private static final DateTimeFormatter W3C = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
	private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

	private final JdbcTemplate jdbcTemplate;
	private final IndexHtmlProvider indexHtmlProvider;

	// ===== 게시물 상세 =====

	@GetMapping(value = "/posts/{postId:\\d+}", produces = MediaType.TEXT_HTML_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<String> post(@PathVariable long postId) {
		List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
			SELECT p.post_id, p.title, p.content, p.writer, p.created_at, c.name AS category_name,
			       e.start_time, e.end_time, e.end_type, e.link AS event_link,
			       h.shopping_mall, h.product_name, h.price, h.link AS hotdeal_link,
			       (SELECT i.url FROM images i WHERE i.post_id = p.post_id ORDER BY i.is_thumbnail DESC, i.image_id LIMIT 1) AS image_url
			FROM posts p
			JOIN statuses s ON s.status_id = p.status_id
			JOIN categories c ON c.category_id = p.category_id
			LEFT JOIN events e ON e.event_id = p.event_id
			LEFT JOIN hotdeals h ON h.hotdeal_id = p.hotdeal_id
			WHERE p.post_id = ? AND s.name = '작성됨'""", postId);

		String base = baseUrl();
		if (rows.isEmpty()) {
			return html(HttpStatus.NOT_FOUND, SeoPage.builder()
				.title("페이지를 찾을 수 없어요 | 이벤터")
				.description("삭제되었거나 없는 게시물입니다.")
				.canonical(base + "/posts/" + postId)
				.robots("noindex")
				.body("<h1>페이지를 찾을 수 없어요</h1><p><a href=\"/\">이벤터 홈으로</a></p>")
				.build());
		}

		Map<String, Object> r = rows.getFirst();
		String title = str(r.get("title"));
		String category = str(r.get("category_name"));
		String content = HtmlSanitizer.sanitize(str(r.get("content")));
		String description = HtmlSanitizer.text(content, 150);
		if (description.isBlank()) {
			description = category + " 소식 - " + title;
		}
		String image = absolute(base, str(r.get("image_url")));
		String url = base + "/posts/" + postId;
		LocalDateTime createdAt = toDateTime(r.get("created_at"));

		StringBuilder body = new StringBuilder()
			.append("<article><nav><a href=\"/\">홈</a> › <a href=\"/posts?categoryName=")
			.append(UriUtils.encodeQueryParam(category, "UTF-8")).append("\">").append(esc(category)).append("</a></nav>")
			.append("<h1>").append(esc(title)).append("</h1>")
			.append("<p>").append(esc(str(r.get("writer")))).append(" · ")
			.append(createdAt == null ? "" : createdAt.toLocalDate()).append("</p>");
		if (r.get("start_time") != null || r.get("end_time") != null || r.get("end_type") != null) {
			body.append("<p>이벤트 기간: ").append(date(r.get("start_time"))).append(" ~ ")
				.append(r.get("end_time") != null ? date(r.get("end_time")) : esc(str(r.get("end_type")))).append("</p>");
		}
		if (r.get("shopping_mall") != null || r.get("price") != null) {
			body.append("<p>").append(esc(str(r.get("shopping_mall")))).append(" ").append(esc(str(r.get("product_name"))));
			if (r.get("price") != null) {
				body.append(" ").append(String.format("%,d원", ((Number)r.get("price")).longValue()));
			}
			body.append("</p>");
		}
		String link = str(r.get("event_link")).isBlank() ? str(r.get("hotdeal_link")) : str(r.get("event_link"));
		if (!link.isBlank() && link.startsWith("http")) {
			body.append("<p><a href=\"").append(esc(link)).append("\" rel=\"nofollow noopener\">바로가기</a></p>");
		}
		body.append("<div>").append(content).append("</div></article>");

		String jsonLd = "{\"@context\":\"https://schema.org\",\"@type\":\"Article\",\"headline\":" + json(title)
			+ ",\"description\":" + json(description)
			+ (image.isBlank() ? "" : ",\"image\":[" + json(image) + "]")
			+ (createdAt == null ? "" : ",\"datePublished\":" + json(createdAt.atZone(SEOUL).format(W3C)))
			+ ",\"author\":{\"@type\":\"Person\",\"name\":" + json(str(r.get("writer"))) + "}"
			+ ",\"mainEntityOfPage\":" + json(url) + "}";

		return html(HttpStatus.OK, SeoPage.builder()
			.title(title + " | 이벤터")
			.description(description)
			.canonical(url)
			.image(image)
			.type("article")
			.jsonLd(jsonLd)
			.body(body.toString())
			.build());
	}

	// ===== 카테고리 목록 =====

	@GetMapping(value = "/posts", produces = MediaType.TEXT_HTML_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<String> category(@RequestParam(required = false) String categoryName,
		@RequestParam(required = false) String domain) {
		String base = baseUrl();
		String name = categoryName == null || categoryName.isBlank() ? "이벤트" : categoryName.trim();
		// 이벤트 분야(AI 분류, 2026-11-20): /posts?categoryName=이벤트&domain=음식·카페
		String dom = domain != null && com.eventorback.ai.Taxonomy.DOMAINS.contains(domain.trim()) ? domain.trim() : null;
		List<Long> ids = jdbcTemplate.queryForList("""
			WITH RECURSIVE tree AS (
			  SELECT category_id FROM categories WHERE name = ?
			  UNION ALL
			  SELECT c.category_id FROM categories c JOIN tree t ON c.parent_category_id = t.category_id)
			SELECT category_id FROM tree""", Long.class, name);
		String url = base + "/posts?categoryName=" + UriUtils.encodeQueryParam(name, "UTF-8")
			+ (dom == null ? "" : "&domain=" + UriUtils.encodeQueryParam(dom, "UTF-8"));
		String heading = dom == null ? name : dom + " 이벤트";
		if (ids.isEmpty()) {
			return html(HttpStatus.NOT_FOUND, SeoPage.builder().title("페이지를 찾을 수 없어요 | 이벤터")
				.description("없는 카테고리입니다.").canonical(url).robots("noindex").body("<h1>없는 카테고리</h1>").build());
		}

		String in = String.join(",", ids.stream().map(String::valueOf).toList());
		List<Map<String, Object>> posts = jdbcTemplate.queryForList("""
			SELECT p.post_id, p.title FROM posts p JOIN statuses s ON s.status_id = p.status_id
			""" + (dom == null ? "" : "JOIN post_insights pi ON pi.post_id = p.post_id AND pi.domain = ? ") + """
			WHERE s.name = '작성됨' AND p.category_id IN (""" + in + ") ORDER BY p.created_at DESC LIMIT 30",
			dom == null ? new Object[0] : new Object[] {dom});

		StringBuilder body = new StringBuilder("<h1>").append(esc(heading)).append("</h1><ul>");
		for (Map<String, Object> p : posts) {
			body.append("<li><a href=\"/posts/").append(p.get("post_id")).append("\">").append(esc(str(p.get("title"))))
				.append("</a></li>");
		}
		body.append("</ul>");
		String description = heading + " 최신 소식을 이벤터에서 모아 보세요. "
			+ String.join(", ", posts.stream().limit(3).map(p -> str(p.get("title"))).toList());

		return html(HttpStatus.OK, SeoPage.builder()
			.title(heading + " 모아보기 | 이벤터")
			.description(HtmlSanitizer.text(description, 150))
			.canonical(url)
			.body(body.toString())
			.build());
	}

	// ===== 사이트맵 =====

	/** 사이트맵 색인: 고정 페이지 + 게시물 사이트맵(4만 개씩 나눔) */
	@GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<String> sitemapIndex() {
		String base = baseUrl();
		long total = countPublishedPosts();
		int pages = (int)Math.max(1, (total + SITEMAP_PAGE_SIZE - 1) / SITEMAP_PAGE_SIZE);
		String now = LocalDateTime.now().atZone(SEOUL).format(W3C);
		StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
			.append("<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n")
			.append("  <sitemap><loc>").append(base).append("/sitemaps/pages.xml</loc><lastmod>").append(now)
			.append("</lastmod></sitemap>\n");
		for (int i = 1; i <= pages; i++) {
			xml.append("  <sitemap><loc>").append(base).append("/sitemaps/posts-").append(i)
				.append(".xml</loc><lastmod>").append(now).append("</lastmod></sitemap>\n");
		}
		xml.append("</sitemapindex>\n");
		return xml(xml.toString());
	}

	/**
	 * 홈·게시판·이벤트 분야·트렌드·축제(2026-11-20: 옛 카테고리 35개 대신 게시판 4 + AI 분야 8).
	 * 옛 카테고리 주소는 화면에서 분야 주소로 넘기므로 사이트맵에 넣지 않는다.
	 */
	@GetMapping(value = "/sitemaps/pages.xml", produces = MediaType.APPLICATION_XML_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<String> sitemapPages() {
		String base = baseUrl();
		StringBuilder xml = urlsetStart().append(url(base + "/", null, "daily", "1.0"));
		for (String board : List.of("이벤트", "핫딜", "자유", "공지")) {
			xml.append(url(base + "/posts?categoryName=" + UriUtils.encodeQueryParam(board, "UTF-8"), null, "daily", "0.8"));
		}
		for (String dom : com.eventorback.ai.Taxonomy.DOMAINS) {
			xml.append(url(base + "/posts?categoryName=" + UriUtils.encodeQueryParam("이벤트", "UTF-8") + "&domain="
				+ UriUtils.encodeQueryParam(dom, "UTF-8"), null, "daily", "0.7"));
		}
		xml.append(url(base + "/insights", null, "daily", "0.6"));
		xml.append(url(base + "/tours/festival2", null, "daily", "0.6"));
		return xml(xml.append("</urlset>\n").toString());
	}

	@GetMapping(value = "/sitemaps/posts-{page:\\d+}.xml", produces = MediaType.APPLICATION_XML_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<String> sitemapPosts(@PathVariable int page) {
		String base = baseUrl();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
			SELECT p.post_id, p.created_at FROM posts p JOIN statuses s ON s.status_id = p.status_id
			WHERE s.name = '작성됨' ORDER BY p.post_id LIMIT ? OFFSET ?""",
			SITEMAP_PAGE_SIZE, (long)(Math.max(page, 1) - 1) * SITEMAP_PAGE_SIZE);
		if (rows.isEmpty() && page > 1) {
			return ResponseEntity.notFound().build();
		}
		StringBuilder xml = urlsetStart();
		for (Map<String, Object> r : rows) {
			LocalDateTime createdAt = toDateTime(r.get("created_at"));
			xml.append(url(base + "/posts/" + r.get("post_id"),
				createdAt == null ? null : createdAt.atZone(SEOUL).format(W3C), null, null));
		}
		return xml(xml.append("</urlset>\n").toString());
	}

	// ===== 공통 =====

	private ResponseEntity<String> html(HttpStatus status, SeoPage page) {
		return ResponseEntity.status(status)
			.contentType(new MediaType(MediaType.TEXT_HTML, java.nio.charset.StandardCharsets.UTF_8))
			.cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS).cachePublic())
			.body(page.render(indexHtmlProvider.get()));
	}

	private static ResponseEntity<String> xml(String body) {
		return ResponseEntity.ok()
			.contentType(new MediaType(MediaType.APPLICATION_XML, java.nio.charset.StandardCharsets.UTF_8))
			.cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
			.body(body);
	}

	private long countPublishedPosts() {
		Long count = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM posts p JOIN statuses s ON s.status_id = p.status_id WHERE s.name = '작성됨'", Long.class);
		return count == null ? 0 : count;
	}

	private static StringBuilder urlsetStart() {
		return new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
			.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
	}

	private static String url(String loc, String lastmod, String changefreq, String priority) {
		StringBuilder s = new StringBuilder("  <url><loc>").append(HtmlUtils.htmlEscape(loc)).append("</loc>");
		if (lastmod != null) {
			s.append("<lastmod>").append(lastmod).append("</lastmod>");
		}
		if (changefreq != null) {
			s.append("<changefreq>").append(changefreq).append("</changefreq>");
		}
		if (priority != null) {
			s.append("<priority>").append(priority).append("</priority>");
		}
		return s.append("</url>\n").toString();
	}

	/** 요청한 주소의 사이트 주소(운영 https://eventor.duckdns.org, 로컬 http://localhost:9080) */
	private static String baseUrl() {
		return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString().replaceAll("/+$", "");
	}

	private static String absolute(String base, String url) {
		if (url == null || url.isBlank()) {
			return "";
		}
		return url.startsWith("http") ? url : base + url;
	}

	private static LocalDateTime toDateTime(Object value) {
		if (value instanceof Timestamp t) {
			return t.toLocalDateTime();
		}
		if (value instanceof LocalDateTime l) {
			return l;
		}
		return null;
	}

	private static String date(Object value) {
		LocalDateTime d = toDateTime(value);
		return d == null ? "" : d.toLocalDate().toString();
	}

	private static String str(Object value) {
		return value == null ? "" : String.valueOf(value);
	}

	private static String esc(String value) {
		return HtmlUtils.htmlEscape(value == null ? "" : value);
	}

	static String json(String value) {
		StringBuilder s = new StringBuilder("\"");
		for (char c : (value == null ? "" : value).toCharArray()) {
			switch (c) {
				case '"' -> s.append("\\\"");
				case '\\' -> s.append("\\\\");
				case '\n' -> s.append("\\n");
				case '\r' -> s.append("\\r");
				case '\t' -> s.append("\\t");
				case '<' -> s.append("\\u003c"); // </script> 로 스크립트를 끊지 못하게
				default -> {
					if (c < 0x20) {
						s.append(String.format("\\u%04x", (int)c));
					} else {
						s.append(c);
					}
				}
			}
		}
		return s.append('"').toString();
	}
}
