package com.eventorback.auth.aspect;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.global.exception.UnauthorizedException;
import com.eventorback.global.exception.UnavailableAuthorizationException;
import com.eventorback.global.exception.UserNotActiveException;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class RoleAuthorizationAspect {
	private final UserRepository userRepository;

	@Before("@annotation(authorizeRole)")
	public void checkUserRole(JoinPoint joinPoint, AuthorizeRole authorizeRole) throws Throwable {

		HttpServletRequest request = ((ServletRequestAttributes)Objects.requireNonNull(
			RequestContextHolder.getRequestAttributes())).getRequest();

		String userIdHeader = request.getHeader("X-User-UserId");
		// 헤더에서 역할 정보 추출

		String rolesHeader = request.getHeader("X-User-Roles");
		if (rolesHeader == null) {
			throw new UnauthorizedException();
		}

		// @AuthorizeRole 에 정의된 역할 목록
		List<String> requiredRoles = Arrays.asList(authorizeRole.value());

		// 헤더에서 역할 목록을 ','로 구분하여 리스트로 변환
		List<String> userRoles = Arrays.asList(rolesHeader.split(","));

		Long userId = Long.parseLong(userIdHeader);
		User user = userRepository.findById(userId).orElseThrow();
		if ("휴면".equals(user.getStatus().getName())) {
			throw new UserNotActiveException();
		}

		// 사용자가 요구되는 역할을 가지고 있는지 확인
		boolean hasRequiredRole = userRoles.stream().anyMatch(requiredRoles::contains);

		if (!hasRequiredRole) {
			throw new UnavailableAuthorizationException();
		}
	}
}
