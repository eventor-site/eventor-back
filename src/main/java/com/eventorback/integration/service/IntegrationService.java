package com.eventorback.integration.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.eventorback.global.exception.AlreadyExistsException;
import com.eventorback.global.exception.BadRequestException;
import com.eventorback.global.exception.NotFoundException;
import com.eventorback.integration.domain.dto.IntegrationRequest;
import com.eventorback.integration.domain.dto.IntegrationResponse;
import com.eventorback.integration.domain.entity.Integration;
import com.eventorback.integration.repository.IntegrationRepository;
import com.eventorback.llm.LlmRequest;
import com.eventorback.llm.LlmService;
import com.eventorback.tour.client.TourApiClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 외부 연동 목록 관리와 연결 점검.
 * 점검은 관리자가 버튼을 누를 때만 한 번 호출한다(외부 API 호출 한도 보호).
 */
@Slf4j
@Service
@Transactional
public class IntegrationService {

	/** 점검 결과. ok=false 면 message 에 이유 */
	public record CheckResult(boolean ok, String message) {
	}

	private final IntegrationRepository integrationRepository;
	private final Environment environment;
	private final Map<String, Supplier<CheckResult>> checkers;

	public IntegrationService(IntegrationRepository integrationRepository, Environment environment,
		TourApiClient tourApiClient, JavaMailSender mailSender, RestClient.Builder restClientBuilder, LlmService llmService,
		@Value("${openApi.tour:}") String tourKey) {
		this.integrationRepository = integrationRepository;
		this.environment = environment;
		RestClient restClient = restClientBuilder.build();
		this.checkers = Map.of(
			"tour", () -> checkTour(tourApiClient, tourKey),
			"gmail", () -> checkMail(mailSender),
			"google-oauth", () -> checkGoogle(restClient),
			"gemini", () -> checkGemini(llmService)
		);
	}

	@Transactional(readOnly = true)
	public List<IntegrationResponse> getIntegrations() {
		return integrationRepository.findAllByOrderBySortOrderAscIntegrationIdAsc().stream().map(this::toResponse).toList();
	}

	public IntegrationResponse create(IntegrationRequest request) {
		if (integrationRepository.existsByCode(request.code().trim())) {
			throw new AlreadyExistsException("이미 있는 코드예요: " + request.code());
		}
		return toResponse(integrationRepository.save(Integration.create(request)));
	}

	public IntegrationResponse update(Long integrationId, IntegrationRequest request) {
		Integration integration = find(integrationId);
		integration.apply(request);
		return toResponse(integration);
	}

	public void delete(Long integrationId) {
		integrationRepository.delete(find(integrationId));
	}

	/** 키가 설정돼 있는지 먼저 보고, 점검 기능이 있으면 실제로 한 번 호출해 본다 */
	public IntegrationResponse check(Long integrationId) {
		Integration integration = find(integrationId);
		Supplier<CheckResult> checker = checkers.get(integration.getCode());
		if (checker == null) {
			throw new BadRequestException("이 연동은 자동 점검 기능이 없어요. 사이트에서 직접 확인해 주세요.");
		}
		List<String> missing = envKeyNames(integration).stream().filter(name -> !configured(name)).toList();
		CheckResult result;
		if (!missing.isEmpty()) {
			result = new CheckResult(false, "환경 변수가 비어 있어요: " + String.join(", ", missing));
		} else {
			try {
				result = checker.get();
			} catch (Exception e) {
				log.warn("연동 점검 실패: {}", integration.getCode(), e);
				result = new CheckResult(false, "호출 실패: " + e.getClass().getSimpleName());
			}
		}
		integration.recordCheck(result.ok(), result.message());
		return toResponse(integration);
	}

	// ===== 점검 =====

	private static CheckResult checkTour(TourApiClient client, String key) {
		String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		Map<String, Object> body = client.searchFestival2(1, "WEB", "이벤터", "json", "R", today, today, key);
		Object response = body == null ? null : body.get("response");
		if (response instanceof Map<?, ?> map && map.get("header") instanceof Map<?, ?> header) {
			Object code = header.get("resultCode");
			return "0000".equals(String.valueOf(code))
				? new CheckResult(true, "정상 응답(resultCode 0000)")
				: new CheckResult(false, "오류 응답: " + code + " " + header.get("resultMsg"));
		}
		return new CheckResult(false, "예상과 다른 응답(인증키 확인 필요)");
	}

	private static CheckResult checkMail(JavaMailSender sender) {
		if (!(sender instanceof JavaMailSenderImpl impl)) {
			return new CheckResult(false, "메일 발송기 설정을 확인할 수 없어요");
		}
		try {
			impl.testConnection();
			return new CheckResult(true, "SMTP 접속·로그인 성공(" + impl.getHost() + ")");
		} catch (Exception e) {
			return new CheckResult(false, "SMTP 로그인 실패: 앱 비밀번호를 확인해 주세요");
		}
	}

	private static CheckResult checkGoogle(RestClient restClient) {
		var res = restClient.get().uri("https://accounts.google.com/.well-known/openid-configuration").retrieve().toBodilessEntity();
		return res.getStatusCode().is2xxSuccessful()
			? new CheckResult(true, "구글 인증 서버 응답 정상·키 설정됨(실제 로그인은 화면에서 확인)")
			: new CheckResult(false, "구글 인증 서버 응답 " + res.getStatusCode().value());
	}

	private static CheckResult checkGemini(LlmService llm) {
		String model = llm.bulkModel();
		var result = llm.call(new LlmRequest(model, "연결 점검이다. ok 에 true 를 넣어 답한다.", "ping",
			Map.of("type", "OBJECT", "properties", Map.of("ok", Map.of("type", "BOOLEAN")), "required", List.of("ok")), false), 0);
		if (result.isEmpty()) {
			return new CheckResult(false, "이벤터 몫 한도를 다 써서 점검하지 않았어요(" + model + ")");
		}
		return result.get().json().path("ok").asBoolean(false)
			? new CheckResult(true, "정상 응답(" + model + ", 오늘 남은 요청 " + llm.remainingToday(model) + ")")
			: new CheckResult(false, "예상과 다른 응답(" + model + ")");
	}

	// ===== 공통 =====

	private Integration find(Long integrationId) {
		return integrationRepository.findById(integrationId).orElseThrow(() -> new NotFoundException("연동을 찾을 수 없어요."));
	}

	private boolean configured(String envName) {
		String value = environment.getProperty(envName);
		return value != null && !value.isBlank();
	}

	private static List<String> envKeyNames(Integration integration) {
		if (integration.getEnvKeys() == null) {
			return List.of();
		}
		return Arrays.stream(integration.getEnvKeys().split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
	}

	private IntegrationResponse toResponse(Integration i) {
		List<IntegrationResponse.EnvKey> keys = envKeyNames(i).stream()
			.map(name -> new IntegrationResponse.EnvKey(name, configured(name)))
			.toList();
		return new IntegrationResponse(i.getIntegrationId(), i.getCode(), i.getName(), i.getProvider(), i.getCategory(),
			i.getPurpose(), i.getStatus(), i.getEligibility(), i.getEligibilityNote(), i.getIcon(), i.getSiteUrl(), i.getConsoleUrl(), i.getDocsUrl(), keys, i.getMemo(),
			i.getSortOrder(), checkers.containsKey(i.getCode()), i.getLastCheckedAt(), i.getLastCheckOk(),
			i.getLastCheckMessage(), i.getUpdatedAt());
	}
}
