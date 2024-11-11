package com.eventorback.user.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sikyeojoback.status.domain.entity.Status;
import com.sikyeojoback.status.exception.StatusNotFoundException;
import com.sikyeojoback.status.repository.StatusRepository;
import com.sikyeojoback.user.domain.dto.request.SignUpRequest;
import com.sikyeojoback.user.domain.dto.response.GetUserByAddShopResponse;
import com.sikyeojoback.user.domain.dto.response.UserTokenInfo;
import com.sikyeojoback.user.domain.entity.User;
import com.sikyeojoback.user.exception.UserNotFoundException;
import com.sikyeojoback.user.repository.UserRepository;
import com.sikyeojoback.user.service.UserService;
import com.sikyeojoback.usergrade.domain.entity.UserGrade;
import com.sikyeojoback.usergrade.repository.UserGradeRepository;
import com.sikyeojoback.userrole.repository.UserRoleRepository;

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
		UserGrade userGrade = userGradeRepository.findByName("일반").orElseThrow(() -> new StatusNotFoundException("일반"));

		String encodedPassword = passwordEncoder.encode(request.password());
		userRepository.save(User.toEntity(status, userGrade, request, encodedPassword));
	}

	@Override
	public UserTokenInfo getUserTokenInfoById(String id) {
		User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
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
