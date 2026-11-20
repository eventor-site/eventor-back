package com.eventorback.global;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.eventorback.global.util.HtmlSanitizer;

class HtmlSanitizerTest {

	@Test
	void 스크립트와_이벤트속성은_지운다() {
		String out = HtmlSanitizer.sanitize("<p onclick=\"x()\">안녕<script>alert(1)</script></p><img src=x onerror=alert(1)>");
		assertThat(out).doesNotContain("script", "onclick", "onerror").contains("안녕");
	}

	@Test
	void 업로드_이미지의_상대주소는_남긴다() {
		assertThat(HtmlSanitizer.sanitize("<img src=\"/uploads/postimage/20261119/a.webp\">"))
			.contains("src=\"/uploads/postimage/20261119/a.webp\"");
	}

	@Test
	void 허용한_사이트의_영상만_남긴다() {
		assertThat(HtmlSanitizer.sanitize("<iframe src=\"https://www.youtube.com/embed/abc\"></iframe>")).contains("youtube.com/embed/abc");
		assertThat(HtmlSanitizer.sanitize("<iframe src=\"https://evil.com/x\"></iframe>")).doesNotContain("iframe");
	}

	@Test
	void 자바스크립트_링크는_지우고_외부링크는_새창으로() {
		String out = HtmlSanitizer.sanitize("<a href=\"javascript:alert(1)\">a</a><a href=\"https://naver.com\">b</a>");
		assertThat(out).doesNotContain("javascript").contains("href=\"https://naver.com\"", "noopener");
	}
}
