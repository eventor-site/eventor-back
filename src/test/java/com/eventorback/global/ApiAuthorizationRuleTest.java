package com.eventorback.global;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;

/**
 * 데이터를 바꾸는 API(POST·PUT·DELETE)는 모두 @AuthorizeRole 이 있어야 한다.
 * 로그인 전에 써야 하는 API만 아래 목록에 이유와 함께 둔다.
 * (2026-11 점검에서 역할·등급·상태 등의 생성·삭제 API가 권한 없이 열려 있던 것을 막기 위한 규칙)
 */
class ApiAuthorizationRuleTest {

	private static final Set<String> PUBLIC_WRITE_APIS = Set.of(
		"AuthController#login",             // 로그인
		"AuthController#logout",            // 로그아웃(쿠키만 지운다)
		"AuthController#activateDormant",   // 휴면 해제(로그인으로 받은 티켓 필요)
		"AuthController#findIdentifier",    // 아이디 찾기(메일로만 알려 줌)
		"AuthController#sendResetCode",     // 비밀번호 재설정 인증번호
		"AuthController#resetPassword",     // 비밀번호 재설정(인증번호 필요)
		"AuthController#googleSignup",      // 구글 가입(가입 대기 티켓 필요)
		"UserController#signup",            // 회원가입(이메일 인증 필요)
		"UserController#checkIdentifier",
		"UserController#checkNickname",
		"UserController#sendEmail",
		"UserController#certifyEmail",
		"StatisticController#increaseVisitor" // 방문자 수 집계
	);

	@Test
	void 데이터를_바꾸는_API는_권한검사가_있다() throws Exception {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

		List<String> violations = new ArrayList<>();
		for (var bean : scanner.findCandidateComponents("com.eventorback")) {
			Class<?> type = Class.forName(bean.getBeanClassName());
			for (Method method : type.getDeclaredMethods()) {
				RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
				if (mapping == null) {
					continue;
				}
				boolean writes = List.of(mapping.method()).stream()
					.anyMatch(m -> m == RequestMethod.POST || m == RequestMethod.PUT || m == RequestMethod.DELETE);
				String key = type.getSimpleName() + "#" + method.getName();
				if (writes && method.getAnnotation(AuthorizeRole.class) == null && !PUBLIC_WRITE_APIS.contains(key)) {
					violations.add(key);
				}
			}
		}
		assertThat(violations).as("권한 검사가 없는 쓰기 API").isEmpty();
	}
}
