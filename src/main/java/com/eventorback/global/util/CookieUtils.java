package com.eventorback.global.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;

@Component
public class CookieUtils {

	public Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, URLEncoder.encode(value, StandardCharsets.UTF_8));
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}

	public Cookie expireCookie(String key) {
		Cookie cookie = new Cookie(key, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		cookie.setHttpOnly(true);

		return cookie;
	}
}
