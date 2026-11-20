package com.eventorback.seo;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class SeoPageTest {

	private static String builtIndexHtml() throws Exception {
		// 프론트 production 빌드(dist/index.html)를 복사해 둔 것
		try (var in = SeoPageTest.class.getResourceAsStream("/seo/index.html")) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	@Test
	void 빌드된_index_html에_제목_설명_OG_본문을_넣는다() throws Exception {
		String html = SeoPage.builder()
			.title("[스타벅스] <별> 이벤트 | 이벤터")
			.description("설명 \"따옴표\"")
			.canonical("https://eventor.duckdns.org/posts/2")
			.image("https://eventor.duckdns.org/uploads/a.png")
			.type("article")
			.jsonLd("{\"@type\":\"Article\"}")
			.body("<h1>본문</h1>")
			.build()
			.render(builtIndexHtml());

		assertThat(html).contains("<title>[스타벅스] &lt;별&gt; 이벤트 | 이벤터</title>");
		assertThat(html).containsOnlyOnce("<meta name=\"description\"");
		assertThat(html).contains("content=\"설명 &quot;따옴표&quot;\"");
		assertThat(html).containsOnlyOnce("property=\"og:title\"");
		assertThat(html).contains("<link rel=\"canonical\" href=\"https://eventor.duckdns.org/posts/2\">");
		assertThat(html).contains("<script type=\"application/ld+json\">{\"@type\":\"Article\"}</script>");
		assertThat(html).contains("<div id=\"root\"><div class=\"seo-prerender\"><h1>본문</h1></div></div>");
		// 빌드 스크립트·검색엔진 소유 확인 태그는 그대로
		assertThat(html).contains("/assets/index-").contains("naver-site-verification").contains("og:site_name");
	}

	@Test
	void 구조화데이터_JSON에서_스크립트를_끊지_못한다() {
		assertThat(SeoController.json("</script><script>alert(1)")).doesNotContain("</script>");
		assertThat(SeoController.json("a\"b\\c\nd")).isEqualTo("\"a\\\"b\\\\c\\nd\"");
	}
}
