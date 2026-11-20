package com.eventorback.auth;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.eventorback.auth.controller.AuthControllerAccess;

class AuthControllerTest {

	@Test
	void 로그인후_돌아갈_주소는_같은_사이트_경로만_허용한다() {
		assertThat(AuthControllerAccess.safeRedirect("/posts/1")).isEqualTo("/posts/1");
		assertThat(AuthControllerAccess.safeRedirect("/search?q=a")).isEqualTo("/search?q=a");

		assertThat(AuthControllerAccess.safeRedirect(null)).isEqualTo("/");
		assertThat(AuthControllerAccess.safeRedirect("https://evil.com")).isEqualTo("/");
		assertThat(AuthControllerAccess.safeRedirect("//evil.com")).isEqualTo("/");
		assertThat(AuthControllerAccess.safeRedirect("/\\evil.com")).isEqualTo("/");
		assertThat(AuthControllerAccess.safeRedirect("/api/auth/logout")).isEqualTo("/");
	}
}
