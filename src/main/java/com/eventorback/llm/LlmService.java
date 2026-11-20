package com.eventorback.llm;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.eventorback.global.config.EventorProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * LLM 호출의 유일한 입구. 모델별 이벤터 몫(분당·하루)을 넘지 않게 막고, 사용량을 llm_usage 에 남긴다.
 *
 * <ul>
 *     <li>하루 한도는 태평양 시간 자정에 초기화되므로 날짜도 그 기준으로 센다.</li>
 *     <li>429 를 받으면 그 모델을 70초 쉰다. 연속 3번이면 30분 쉰다(같은 프로젝트를 쓰는 다른 서비스 보호).</li>
 *     <li>예산이 없으면 호출하지 않고 {@link Optional#empty()} — 작업은 다음 주기로 밀린다.</li>
 * </ul>
 */
@Slf4j
@Service
public class LlmService {
	public static final ZoneId QUOTA_ZONE = ZoneId.of("America/Los_Angeles");

	private final LlmClient client;
	private final EventorProperties.Gemini config;
	private final JdbcTemplate jdbcTemplate;
	private final Map<String, Deque<Long>> recent = new HashMap<>();
	private final Map<String, Long> cooldownUntil = new HashMap<>();
	private final Map<String, Integer> consecutive429 = new HashMap<>();

	public LlmService(LlmClient client, EventorProperties properties, JdbcTemplate jdbcTemplate) {
		this.client = client;
		this.config = properties.gemini();
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean enabled() {
		return client.enabled() && config != null;
	}

	public boolean jobsEnabled() {
		return enabled() && config.jobsEnabled();
	}

	public String bulkModel() {
		return config.bulkModel();
	}

	public String qualityModel() {
		return config.qualityModel();
	}

	/** 오늘 남은 요청 수(예산 기준) */
	public int remainingToday(String model) {
		EventorProperties.Budget budget = config == null ? null : config.budget(model);
		return budget == null ? 0 : Math.max(0, budget.rpd() - usedToday(model));
	}

	/**
	 * 예산 안에서 한 번 호출한다.
	 *
	 * @param reserve 오늘 이만큼은 남겨 둔다(사용자 요청용 몫을 지키기 위해 배경 작업이 쓰는 값)
	 * @return 예산이 없으면 empty
	 * @throws LlmException 호출 실패(429 포함)
	 */
	public Optional<LlmResult> call(LlmRequest request, int reserve) {
		if (!enabled() || !acquire(request.model(), reserve)) {
			return Optional.empty();
		}
		try {
			LlmResult result = client.generate(request);
			consecutive429.remove(request.model());
			record(request.model(), result.inputTokens(), result.outputTokens(), result.toolTokens(), false, false);
			return Optional.of(result);
		} catch (LlmException e) {
			record(request.model(), 0, 0, 0, true, e.isRateLimited());
			if (e.isRateLimited()) {
				int n = consecutive429.merge(request.model(), 1, Integer::sum);
				long pause = n >= 3 ? 30 * 60_000L : 70_000L;
				synchronized (this) {
					cooldownUntil.put(request.model(), System.currentTimeMillis() + pause);
				}
				log.warn("Gemini 한도 초과({}), {}초 쉼", request.model(), pause / 1000);
			}
			throw e;
		}
	}

	private synchronized boolean acquire(String model, int reserve) {
		EventorProperties.Budget budget = config.budget(model);
		if (budget == null) {
			return false;
		}
		long now = System.currentTimeMillis();
		if (cooldownUntil.getOrDefault(model, 0L) > now) {
			return false;
		}
		Deque<Long> window = recent.computeIfAbsent(model, k -> new ArrayDeque<>());
		while (!window.isEmpty() && window.peekFirst() < now - 60_000) {
			window.pollFirst();
		}
		if (window.size() >= budget.rpm()) {
			return false;
		}
		if (usedToday(model) + reserve >= budget.rpd()) {
			return false;
		}
		window.addLast(now);
		return true;
	}

	private int usedToday(String model) {
		List<Integer> rows = jdbcTemplate.queryForList(
			"SELECT requests FROM llm_usage WHERE usage_date = ? AND model = ?", Integer.class, today(), model);
		return rows.isEmpty() ? 0 : rows.getFirst();
	}

	private void record(String model, long in, long out, long tool, boolean error, boolean rateLimited) {
		jdbcTemplate.update("""
				INSERT INTO llm_usage (usage_date, model, requests, input_tokens, output_tokens, tool_tokens, errors, rate_limited)
				VALUES (?, ?, 1, ?, ?, ?, ?, ?)
				ON DUPLICATE KEY UPDATE requests = requests + 1, input_tokens = input_tokens + VALUES(input_tokens),
				  output_tokens = output_tokens + VALUES(output_tokens), tool_tokens = tool_tokens + VALUES(tool_tokens),
				  errors = errors + VALUES(errors), rate_limited = rate_limited + VALUES(rate_limited)
				""", today(), model, in, out, tool, error ? 1 : 0, rateLimited ? 1 : 0);
	}

	public static LocalDate today() {
		return LocalDate.now(QUOTA_ZONE);
	}

	/** 관리자 화면: 오늘 모델별 사용량·예산 */
	public List<Map<String, Object>> usageToday() {
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(
			"SELECT model, requests, input_tokens, output_tokens, tool_tokens, errors, rate_limited FROM llm_usage WHERE usage_date = ?",
			today());
		Map<String, Map<String, Object>> byModel = new HashMap<>();
		rows.forEach(r -> byModel.put((String)r.get("model"), r));
		return config.budgets().entrySet().stream().map(e -> {
			Map<String, Object> m = new HashMap<>(byModel.getOrDefault(e.getKey(), Map.of("model", e.getKey(), "requests", 0)));
			m.put("model", e.getKey());
			m.put("rpm", e.getValue().rpm());
			m.put("rpd", e.getValue().rpd());
			Long cd = cooldownUntil.get(e.getKey());
			m.put("cooldownSeconds", cd == null ? 0 : Math.max(0, (cd - System.currentTimeMillis()) / 1000));
			return m;
		}).toList();
	}
}
