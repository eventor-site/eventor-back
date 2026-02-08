package com.eventorback.global.config;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.eventorback.auth.filter.LoginFilter;
import com.eventorback.auth.filter.LogoutFilter;
import com.eventorback.auth.repository.RefreshTokenRepository;
import com.eventorback.auth.service.AppCustomUserDetailsService;
import com.eventorback.auth.utils.JwtUtils;
import com.eventorback.global.util.CookieUtils;
import com.eventorback.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final AuthenticationConfiguration authenticationConfiguration;
	private final CookieUtils cookieUtils;
	private final JwtUtils jwtUtils;
	private final AppCustomUserDetailsService userDetailsService;
	private final RefreshTokenRepository refreshTokenRepository;
	// private final CustomOAuth2UserService customOAuth2UserService;
	// private final CustomSuccessHandler customSuccessHandler;

	@Value("${spring.jwt.access-token.expires-in}")
	private Long accessTokenExpiresIn;
	@Value("${spring.jwt.refresh-token.expires-in}")
	private Long refreshTokenExpiresIn;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, UserService userService) throws Exception {
		return http
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)

			.authorizeHttpRequests((auth) -> auth
					.requestMatchers("/**", "/js/**", "/css/**", "/images/**", "/favicon.ico").permitAll()
				// .anyRequest().authenticated()
			)

			//
			// .oauth2Login((oauth2) -> oauth2
			//
			// 	.userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
			// 		.userService(customOAuth2UserService))
			// 	.successHandler(customSuccessHandler)
			//
			// )

			// .addFilterAfter(new JWTFilter(jwtUtils), OAuth2LoginAuthenticationFilter.class)

			.addFilterAt(
				new LoginFilter(
					authenticationManager(authenticationConfiguration),
					jwtUtils,
					accessTokenExpiresIn,
					refreshTokenExpiresIn,
					refreshTokenRepository,
					userService
				),
				UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new LogoutFilter(cookieUtils, refreshTokenRepository),
				org.springframework.security.web.authentication.logout.LogoutFilter.class)
			.userDetailsService(userDetailsService)

			.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {

				CorsConfiguration configuration = new CorsConfiguration();

				configuration.setAllowedOrigins(List.of("https://eventor.kr"));
				configuration.setAllowedMethods(Collections.singletonList("*"));
				configuration.setAllowCredentials(true);
				configuration.setAllowedHeaders(Collections.singletonList("*"));
				configuration.setMaxAge(3600L);

				configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
				configuration.setExposedHeaders(Collections.singletonList("Authorization"));

				return configuration;
			}))
			.build();

	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}
