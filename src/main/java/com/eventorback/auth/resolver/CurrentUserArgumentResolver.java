package com.eventorback.auth.resolver;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.eventorback.auth.annotation.CurrentUser;
import com.eventorback.user.domain.dto.CurrentUserDto;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;

public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CurrentUser.class) && parameter.getParameterType()
			.equals(CurrentUserDto.class);
	}

	@Override
	public Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer,
		@NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

		// 헤더 값 추출
		String userIdHeader = request.getHeader("X-User-userId");
		String userRolesHeader = request.getHeader("X-User-Roles");

		// userId 파싱
		Long userId = userIdHeader != null ? Long.parseLong(userIdHeader) : null;

		// roles 파싱
		List<String> roles;
		if (userRolesHeader != null) {
			roles = Arrays.asList(userRolesHeader.replaceAll("[\\[\\]\\s]", "").split(","));

			return CurrentUserDto.builder()
				.userId(userId)
				.roles(roles)
				.build();
		} else {
			return null;
		}

	}
}
