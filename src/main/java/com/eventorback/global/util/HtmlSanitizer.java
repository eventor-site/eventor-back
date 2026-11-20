package com.eventorback.global.util;

import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

/**
 * 게시물 본문 HTML 정화(저장할 때). 스크립트·이벤트 속성·위험한 링크를 없애고, 영상 iframe 은 허용한 사이트만 남긴다.
 * 프론트(DOMPurify)와 SEO HTML 에서도 한 번 더 정화한다.
 */
public final class HtmlSanitizer {
	private static final Pattern ALLOWED_IFRAME =
		Pattern.compile("^https://(www\\.)?(youtube\\.com|youtube-nocookie\\.com|player\\.vimeo\\.com|tv\\.naver\\.com)/.*");

	private static final Safelist SAFELIST = Safelist.relaxed()
		.addTags("iframe", "video", "source", "hr", "del", "s", "mark")
		.addAttributes("iframe", "src", "width", "height", "allow", "allowfullscreen", "frameborder")
		.addAttributes("video", "src", "controls", "playsinline", "width", "height")
		.addAttributes("source", "src", "type")
		.addAttributes("img", "loading")
		.addAttributes(":all", "class")
		.addProtocols("img", "src", "http", "https")
		.addProtocols("a", "href", "http", "https", "mailto")
		.preserveRelativeLinks(true);

	private HtmlSanitizer() {
	}

	public static String sanitize(String html) {
		if (html == null || html.isBlank()) {
			return html;
		}
		// 상대 주소(/uploads/...)를 살리려면 기준 주소가 있어야 한다. 결과에는 붙지 않는다
		String cleaned = Jsoup.clean(html, "https://eventor.local", SAFELIST,
			new Document.OutputSettings().prettyPrint(false));
		Document doc = Jsoup.parseBodyFragment(cleaned);
		for (Element iframe : doc.select("iframe")) {
			if (!ALLOWED_IFRAME.matcher(iframe.attr("src")).matches()) {
				iframe.remove();
			}
		}
		for (Element a : doc.select("a[href]")) {
			a.attr("rel", "noopener noreferrer nofollow");
			a.attr("target", "_blank");
		}
		doc.outputSettings().prettyPrint(false);
		return doc.body().html();
	}

	/** 태그를 뺀 글자만(SEO 설명문, 검색용) */
	public static String text(String html, int maxLength) {
		if (html == null) {
			return "";
		}
		String text = Jsoup.parse(html).text().replaceAll("\\s+", " ").trim();
		return text.length() > maxLength ? text.substring(0, maxLength - 1) + "…" : text;
	}
}
