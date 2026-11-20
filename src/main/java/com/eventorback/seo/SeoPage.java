package com.eventorback.seo;

import org.springframework.web.util.HtmlUtils;

import lombok.Builder;

/**
 * React index.html 에 검색엔진용 정보를 넣는다.
 * - &lt;title&gt;, description, canonical, robots, Open Graph·트위터 카드, JSON-LD 를 &lt;head&gt; 에서 바꾸거나 더한다
 * - 본문은 &lt;div id="root"&gt; 안에 넣는다. React 가 뜨면 그 자리를 새로 그린다
 */
@Builder
public record SeoPage(String title, String description, String canonical, String image, String type, String robots,
                      String jsonLd, String body) {

	public String render(String indexHtml) {
		String html = indexHtml;
		html = html.replaceAll("(?s)<title>.*?</title>", "<title>" + esc(title) + "</title>");
		// 기본 설명·OG 태그는 지우고 이 페이지 것으로 넣는다
		html = html.replaceAll("(?i)<meta\\s+name=\"description\"[^>]*>\\s*", "");
		html = html.replaceAll("(?i)<meta\\s+property=\"og:(title|description|type|image|url)\"[^>]*>\\s*", "");

		StringBuilder head = new StringBuilder("\n");
		head.append(meta("name", "description", description));
		if (robots != null) {
			head.append(meta("name", "robots", robots));
		}
		head.append("<link rel=\"canonical\" href=\"").append(esc(canonical)).append("\">\n");
		head.append(meta("property", "og:type", type == null ? "website" : type));
		head.append(meta("property", "og:title", title));
		head.append(meta("property", "og:description", description));
		head.append(meta("property", "og:url", canonical));
		if (image != null && !image.isBlank()) {
			head.append(meta("property", "og:image", image));
			head.append(meta("name", "twitter:card", "summary_large_image"));
		}
		if (jsonLd != null) {
			head.append("<script type=\"application/ld+json\">").append(jsonLd).append("</script>\n");
		}
		html = html.replaceFirst("(?i)</head>", java.util.regex.Matcher.quoteReplacement(head + "</head>"));

		String root = "<div id=\"root\"><div class=\"seo-prerender\">" + (body == null ? "" : body) + "</div></div>";
		return html.replaceFirst("<div id=\"root\"></div>", java.util.regex.Matcher.quoteReplacement(root));
	}

	private static String meta(String attr, String key, String value) {
		return "<meta " + attr + "=\"" + key + "\" content=\"" + esc(value) + "\">\n";
	}

	private static String esc(String value) {
		return HtmlUtils.htmlEscape(value == null ? "" : value);
	}
}
