package com.eventorback.global.config;

import java.time.Duration;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 애플리케이션 설정(application.yml 의 eventor.*).
 * 환경마다 달라지는 값은 비밀값(JWT, 구글 키)과 쿠키 Secure 여부뿐이다. 경로·도메인은 요청에서 얻는다.
 */
@ConfigurationProperties("eventor")
public record EventorProperties(
	Jwt jwt,
	Cookie cookie,
	Google google,
	Storage storage,
	Gemini gemini
) {

	/**
	 * @param secret     HS256 서명 키(32바이트 이상)
	 * @param accessTtl  액세스 토큰 유효 시간
	 * @param refreshTtl 리프레시 토큰 유효 시간
	 */
	public record Jwt(String secret, Duration accessTtl, Duration refreshTtl) {
	}

	/**
	 * @param secure HTTPS에서만 쿠키 전송(운영 true, 로컬 http 개발은 false)
	 */
	public record Cookie(boolean secure) {
	}

	public record Google(String clientId, String clientSecret) {
	}

	/**
	 * @param root            업로드 파일 저장 루트(어디서나 ./uploads, 운영 컨테이너는 이 폴더를 마운트)
	 * @param fallbackBaseUrl 로컬에 파일이 없을 때 읽어 올 사이트 주소(로컬 개발 전용, 비우면 끔)
	 */
	public record Storage(String root, String fallbackBaseUrl) {
	}

	/**
	 * Gemini API(Google AI Studio, 무료 등급). 키가 비어 있으면 LLM 기능을 쓰지 않는다.
	 * 무료 한도는 프로젝트 단위라 다른 서비스(trendist 등)와 같은 프로젝트일 수 있으므로, 이벤터는 budgets 만큼만 쓴다.
	 *
	 * @param apiKey       GEMINI_API_KEY
	 * @param bulkModel    대량 작업(분류·링크 읽기·제보 채움) 모델. 무료 10 RPM / 500 RPD
	 * @param qualityModel 품질이 중요한 소량 작업(주간 리포트) 모델. 무료 5 RPM / 20 RPD
	 * @param timeout      호출 제한 시간
	 * @param jobsEnabled  자동 작업(스케줄러) 켜기
	 * @param budgets      모델별 이벤터 몫(rpm, rpd). 없는 모델은 호출하지 않는다
	 */
	public record Gemini(String apiKey, String bulkModel, String qualityModel, Duration timeout, boolean jobsEnabled,
						 Map<String, Budget> budgets) {
		public boolean enabled() {
			return apiKey != null && !apiKey.isBlank();
		}

		public Budget budget(String model) {
			return budgets == null ? null : budgets.get(model);
		}
	}

	/** @param rpm 분당 요청 수, @param rpd 하루(태평양 시간 기준) 요청 수 */
	public record Budget(int rpm, int rpd) {
	}
}
