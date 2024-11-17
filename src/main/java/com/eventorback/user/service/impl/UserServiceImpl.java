package com.eventorback.user.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.exception.StatusNotFoundException;
import com.eventorback.status.repository.StatusRepository;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.response.GetUserByAddShopResponse;
import com.eventorback.user.domain.dto.response.UserTokenInfo;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.exception.UserNotFoundException;
import com.eventorback.user.repository.UserRepository;
import com.eventorback.user.service.UserService;
import com.eventorback.usergrade.domain.entity.UserGrade;
import com.eventorback.usergrade.repository.UserGradeRepository;
import com.eventorback.userrole.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserGradeRepository userGradeRepository;
	private final UserRoleRepository userRoleRepository;
	private final StatusRepository statusRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void signUp(SignUpRequest request) {
		Status status = statusRepository.findByName("활성").orElseThrow(() -> new StatusNotFoundException("활성"));
		UserGrade userGrade = userGradeRepository.findByName("1단계").orElseThrow(() -> new StatusNotFoundException("1단계"));

		String encodedPassword = passwordEncoder.encode(request.password());
		userRepository.save(User.toEntity(status, userGrade, request, encodedPassword));
	}

	@Override
	public UserTokenInfo getUserTokenInfoById(String identifier) {
		User user = userRepository.findByIdentifier(identifier).orElseThrow(() -> new UserNotFoundException(identifier));
		List<String> userRoleNames = userRoleRepository.findRolesByUserId(user.getUserId())
			.stream()
			.map(userRole -> userRole.getRole().getName())
			.toList();

		return UserTokenInfo.fromEntity(user, userRoleNames);
	}

	@Override
	public List<GetUserByAddShopResponse> searchUserById(String keyword) {
		return userRepository.searchUserById(keyword);
	}
}
