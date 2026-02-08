package com.eventorback.oauth.service.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eventorback.auth.dto.entity.RefreshToken;
import com.eventorback.auth.repository.RefreshTokenRepository;
import com.eventorback.auth.utils.JwtUtils;
import com.eventorback.global.exception.ServerException;
import com.eventorback.oauth.client.GoogleProfileClient;
import com.eventorback.oauth.client.GoogleTokenClient;
import com.eventorback.oauth.client.KakaoProfileClient;
import com.eventorback.oauth.client.KakaoTokenClient;
import com.eventorback.oauth.client.NaverProfileClient;
import com.eventorback.oauth.client.NaverTokenClient;
import com.eventorback.oauth.config.OauthAttributes;
import com.eventorback.oauth.config.OauthProvider;
import com.eventorback.oauth.dto.OauthTokenResponse;
import com.eventorback.oauth.dto.UserProfile;
import com.eventorback.oauth.repository.InMemoryOauthRepository;
import com.eventorback.oauth.service.OauthService;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLoginAtRequest;
import com.eventorback.user.domain.dto.response.GetUserOauth;
import com.eventorback.user.domain.dto.response.OauthDto;
import com.eventorback.user.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthServiceImpl implements OauthService {
	private final JwtUtils jwtUtils;
	private final RefreshTokenRepository refreshTokenRepository;
	private final InMemoryOauthRepository inMemoryProviderRepository;
	private final UserService userService;

	private final NaverTokenClient naverTokenClient;
	private final NaverProfileClient naverProfileClient;

	private final KakaoTokenClient kakaoTokenClient;
	private final KakaoProfileClient kakaoProfileClient;

	private final GoogleTokenClient googleTokenClient;
	private final GoogleProfileClient googleProfileClient;

	@Value("${spring.jwt.access-token.expires-in}")
	private Long accessTokenExpiresIn;
	@Value("${spring.jwt.refresh-token.expires-in}")
	private Long refreshTokenExpiresIn;

	@Value("${spring.security.oauth2.client.registration.kakao.admin-key}")
	private String adminKey;

	public String authentication(String registrationId) {
		OauthProvider oauthClient = inMemoryProviderRepository.findByProviderName(registrationId);

		// Redirect URL 생성
		String url = buildRedirectUrl(registrationId, oauthClient);
		return String.format("redirect:%s", url);
	}

	private String buildRedirectUrl(String registrationId, OauthProvider oauthClient) {
		if (registrationId.equals("google")) {
			return String.format(
				"%s?response_type=code&client_id=%s&redirect_uri=%s&scope=%s&state=%s",
				oauthClient.getAuthorizationUri(),
				oauthClient.getClientId(),
				oauthClient.getRedirectUri(),
				URLEncoder.encode(oauthClient.getScope(), StandardCharsets.UTF_8),
				UUID.randomUUID());
		} else {
			return String.format("%s?response_type=code&client_id=%s&state=%s&redirect_uri=%s",
				oauthClient.getAuthorizationUri(),
				oauthClient.getClientId(),
				UUID.randomUUID(),
				oauthClient.getRedirectUri());
		}
	}

	public SignUpRequest getToken(String registrationId, String code) {

		// 프론트에서 넘어온 provider 이름을 통해 InMemoryProviderRepository 에서 OauthProvider 가져오기
		OauthProvider provider = inMemoryProviderRepository.findByProviderName(registrationId);

		// access token 가져오기
		if (registrationId.equals("naver")) {
			OauthTokenResponse tokenResponse = naverTokenClient.getToken(provider.getAuthorizationGrantType(),
				provider.getClientId(), provider.getClientSecret(), code, UUID.randomUUID().toString());

			UserProfile userProfile = OauthAttributes.extract(provider.getClientName(),
				getUserAttributes(registrationId, tokenResponse));

			return SignUpRequest.fromUserProfile(userProfile);
		} else if (registrationId.equals("kakao")) {
			OauthTokenResponse tokenResponse = kakaoTokenClient.getToken(provider.getAuthorizationGrantType(),
				provider.getClientId(), provider.getRedirectUri(), code);

			UserProfile userProfile = OauthAttributes.extract(provider.getClientName(),
				getUserAttributes(registrationId, tokenResponse));
			return SignUpRequest.fromUserProfile(userProfile);
		} else {
			OauthTokenResponse tokenResponse = googleTokenClient.getToken(provider.getAuthorizationGrantType(),
				provider.getClientId(), provider.getClientSecret(), code, provider.getRedirectUri());

			UserProfile userProfile = OauthAttributes.extract(provider.getClientName(),
				getUserAttributes(registrationId, tokenResponse));
			return SignUpRequest.fromUserProfile(userProfile);
		}

	}

	public Map<String, Object> getUserAttributes(String registrationId, OauthTokenResponse tokenResponse) {
		// "Bearer {accessToken}" 형식으로 Authorization 헤더 설정
		String authorizationHeader = "Bearer " + tokenResponse.getAccessToken();

		// ProfileClient 를 사용하여 사용자 프로필 정보 가져오기
		if (registrationId.equals("naver")) {
			return naverProfileClient.getUserProfile(authorizationHeader);
		} else if (registrationId.equals("kakao")) {
			authorizationHeader = "KakaoAK " + adminKey;

			return kakaoProfileClient.getUserProfile(authorizationHeader, "user_id",
				getSubFromIdToken(tokenResponse.getIdToken()));

		} else if (registrationId.equals("google")) {
			return googleProfileClient.getUserProfile(authorizationHeader);
		}
		return null;
	}

	public static Long getSubFromIdToken(String idToken) {
		try {
			// ID 토큰은 3개의 부분으로 나뉘어져 있음: 헤더.페이로드.서명
			String[] tokenParts = idToken.split("\\.");
			if (tokenParts.length < 2) {
				throw new IllegalArgumentException("Invalid ID token format");
			}

			// Base64 디코딩 후 JSON 형식의 페이로드 추출
			String payload = new String(Base64.getUrlDecoder().decode(tokenParts[1]));

			// JSON 파싱 후 sub 값 추출
			JSONObject payloadJson = new JSONObject(payload);
			return Long.parseLong(payloadJson.getString("sub"));
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse sub from ID token", e);
		}
	}

	public Boolean existsByOauth(OauthDto request) {
		return userService.existsByOauth(request);
	}

	public void oauthSignup(SignUpRequest request) {
		userService.signup(request);
	}

	public void oauthLogin(OauthDto request, HttpServletResponse response) {
		GetUserOauth user = userService.getOAuthInfoByOauth(request);
		String urlWithTokens;

		if ("탈퇴".equals(user.statusName()) || "휴면".equals(user.statusName())) {
			urlWithTokens = createRedirectUrl("null", "null", user.oauthId(), user.statusName());
		} else {
			Long userId = user.userId();
			List<String> roles = user.roles();

			String accessToken = jwtUtils.generateAccessToken(userId, roles, accessTokenExpiresIn);
			String refreshToken = jwtUtils.generateRefreshToken(refreshTokenExpiresIn);

			refreshTokenRepository.deleteByUserId(userId);

			refreshTokenRepository.save(
				new RefreshToken(refreshToken.replace("Bearer ", ""), userId, roles, refreshTokenExpiresIn));

			userService.updateLoginAt(new UpdateLoginAtRequest(userId, LocalDateTime.now()));

			urlWithTokens = createRedirectUrl(accessToken, refreshToken, "", "");
		}

		try {
			response.sendRedirect(urlWithTokens);
		} catch (IOException e) {
			throw new ServerException();
		}

	}

	public String createRedirectUrl(String accessToken, String refreshToken, String oauthId, String error) {
		// 클라이언트로 리다이렉트 (토큰 포함)
		String redirectUrl = "https://eventor.kr/auth/oauth2/login";

		return String.format("%s?accessToken=%s&refreshToken=%s&oauthId=%s&error=%s",
			redirectUrl,
			URLEncoder.encode(accessToken, StandardCharsets.UTF_8),
			URLEncoder.encode(refreshToken, StandardCharsets.UTF_8),
			URLEncoder.encode(oauthId, StandardCharsets.UTF_8),
			URLEncoder.encode(error, StandardCharsets.UTF_8));
	}

}
