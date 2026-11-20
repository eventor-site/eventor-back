package com.eventorback.auth.google;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.eventorback.global.config.EventorProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * 구글 OAuth 2.0(Authorization Code) 호출.
 * 인가 주소 → (사용자 동의) → 콜백의 code를 토큰으로 교환 → userinfo로 프로필 조회.
 */
@Slf4j
@Component
public class GoogleOAuthClient {
	private static final String AUTHORIZE_URI = "https://accounts.google.com/o/oauth2/v2/auth";
	private static final String TOKEN_URI = "https://oauth2.googleapis.com/token";
	private static final String USERINFO_URI = "https://openidconnect.googleapis.com/v1/userinfo";
	private static final ParameterizedTypeReference<Map<String, Object>> MAP = new ParameterizedTypeReference<>() {
	};

	private final EventorProperties.Google google;
	private final RestClient restClient;

	public GoogleOAuthClient(EventorProperties properties, RestClient.Builder builder) {
		this.google = properties.google();
		this.restClient = builder.build();
	}

	public record GoogleProfile(String sub, String email, boolean emailVerified, String name) {
	}

	public String authorizeUrl(String redirectUri, String state) {
		return UriComponentsBuilder.fromUriString(AUTHORIZE_URI)
			.queryParam("response_type", "code")
			.queryParam("client_id", google.clientId())
			.queryParam("redirect_uri", redirectUri)
			.queryParam("scope", "openid email profile")
			.queryParam("state", state)
			.queryParam("prompt", "select_account")
			.encode()
			.build()
			.toUriString();
	}

	/**
	 * @throws GoogleLoginException 구글 호출 실패(잘못된 code, 키 오류, 네트워크 등)
	 */
	public GoogleProfile fetchProfile(String code, String redirectUri) {
		try {
			MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
			form.add("grant_type", "authorization_code");
			form.add("code", code);
			form.add("client_id", google.clientId());
			form.add("client_secret", google.clientSecret());
			form.add("redirect_uri", redirectUri);

			Map<String, Object> token = restClient.post().uri(TOKEN_URI)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(form)
				.retrieve()
				.body(MAP);
			if (token == null || token.get("access_token") == null) {
				throw new GoogleLoginException("구글 토큰 응답에 access_token이 없습니다.");
			}

			Map<String, Object> info = restClient.get().uri(USERINFO_URI)
				.header("Authorization", "Bearer " + token.get("access_token"))
				.retrieve()
				.body(MAP);
			if (info == null || info.get("sub") == null) {
				throw new GoogleLoginException("구글 사용자 정보에 sub가 없습니다.");
			}

			return new GoogleProfile(
				String.valueOf(info.get("sub")),
				info.get("email") == null ? null : String.valueOf(info.get("email")),
				Boolean.TRUE.equals(info.get("email_verified")),
				info.get("name") == null ? null : String.valueOf(info.get("name")));
		} catch (RestClientException e) {
			log.warn("구글 로그인 호출 실패: {}", e.getMessage());
			throw new GoogleLoginException("구글 로그인 처리 중 오류가 발생했습니다.");
		}
	}

	public static class GoogleLoginException extends RuntimeException {
		public GoogleLoginException(String message) {
			super(message);
		}
	}
}
