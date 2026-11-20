package com.eventorback.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Spring Security는 보안 헤더와 비밀번호 인코더만 쓴다.
 * 인증은 {@code JwtAuthenticationFilter}(쿠키 JWT), 권한은 {@code @AuthorizeRole} AOP가 맡는다.
 * <p>
 * CSRF: 인증 쿠키가 SameSite=Lax라 다른 사이트에서 보낸 POST·PUT·DELETE에는 쿠키가 붙지 않는다.
 * CORS: 프론트와 API가 같은 도메인(운영 nginx, 로컬 Vite 프록시)이라 허용하지 않는다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.requestCache(AbstractHttpConfigurer::disable)
			.anonymous(AbstractHttpConfigurer::disable)
			.headers(headers -> headers
				.frameOptions(frame -> frame.sameOrigin())
				.referrerPolicy(ref -> ref.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)))
			.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
			.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
