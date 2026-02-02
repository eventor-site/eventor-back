package com.eventorback.auth.service;

import java.util.Objects;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.eventorback.auth.dto.custom.AppCustomUserDetails;
import com.eventorback.user.domain.dto.response.GetUserAuth;
import com.eventorback.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 정보를 로드하여 Spring Security 의 {@link UserDetails} 객체를 반환하는 서비스입니다.
 * 사용자 정보를 외부 클라이언트에서 가져와서 {@link UserDetails} 구현체를 반환합니다.
 */
@Service
@RequiredArgsConstructor
public class AppCustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	/**
	 * 사용자 아이디를 기반으로 {@link UserDetails}를 로드합니다.
	 * 외부 서비스에서 사용자 정보를 가져오고, 사용자의 상태에 따라 적절한 {@link UserDetails} 객체를 반환합니다.
	 */
	@Override
	public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
		GetUserAuth user = userRepository.getAuthByIdentifier(identifier);

		if (Objects.isNull(user)) {
			throw new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다.");
		}

		return new AppCustomUserDetails(user);
	}
}
