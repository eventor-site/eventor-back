package com.eventorback.user.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.role.domain.entity.Role;
import com.eventorback.role.exception.RoleNotFoundException;
import com.eventorback.role.repository.RoleRepository;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.exception.StatusNotFoundException;
import com.eventorback.status.repository.StatusRepository;
import com.eventorback.user.domain.dto.request.ModifyPasswordRequest;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLastLoginTimeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserRequest;
import com.eventorback.user.domain.dto.response.GetUserByAddShopResponse;
import com.eventorback.user.domain.dto.response.GetUserResponse;
import com.eventorback.user.domain.dto.response.UserTokenInfo;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.exception.UserNotFoundException;
import com.eventorback.user.repository.UserRepository;
import com.eventorback.user.service.UserService;
import com.eventorback.usergrade.domain.entity.UserGrade;
import com.eventorback.usergrade.repository.UserGradeRepository;
import com.eventorback.userrole.domain.entity.UserRole;
import com.eventorback.userrole.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserGradeRepository userGradeRepository;
	private final RoleRepository roleRepository;
	private final UserRoleRepository userRoleRepository;
	private final StatusRepository statusRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void signUp(SignUpRequest request) {
		Status status = statusRepository.findOrCreateStatus("회원", "활성");
		UserGrade userGrade = userGradeRepository.findByName("1단계")
			.orElseThrow(() -> new StatusNotFoundException("1단계"));

		String encodedPassword = passwordEncoder.encode(request.password());
		User user = userRepository.save(User.toEntity(status, userGrade, request, encodedPassword));

		// 회원 가입시 기본 권한 데이터 설정
		Role role = roleRepository.findByName("member").orElseThrow(() -> new RoleNotFoundException("member"));
		userRoleRepository.save(UserRole.toEntity(user, role));
	}

	@Override
	public UserTokenInfo getUserTokenInfoByIdentifier(String identifier) {
		User user = userRepository.findByIdentifier(identifier)
			.orElseThrow(() -> new UserNotFoundException(identifier));
		List<String> userRoleNames = userRoleRepository.findAllByUserUserId(user.getUserId())
			.stream().map(userRole -> userRole.getRole().getName()).toList();

		return UserTokenInfo.fromEntity(user, userRoleNames);
	}

	@Override
	public List<GetUserByAddShopResponse> searchUserById(String keyword) {
		return userRepository.searchUserById(keyword);
	}

	@Override
	public GetUserResponse getUserInfo(Long userId) {
		return userRepository.getUserInfo(userId).orElseThrow(() -> new UserNotFoundException(userId));
	}

	@Override
	public void updateUser(Long userId, UpdateUserRequest request) {
		User user = userRepository.getUser(userId).orElseThrow(() -> new UserNotFoundException(userId));
		user.updateUser(request);
	}

	@Override
	public void updateLastLoginTime(UpdateLastLoginTimeRequest request) {
		User user = userRepository.getUser(request.userId())
			.orElseThrow(() -> new UserNotFoundException(request.userId()));
		user.updateLastLoginTime(request);
	}

	@Override
	public String modifyPassword(Long userId, ModifyPasswordRequest request) {
		User user = userRepository.getUser(userId).orElseThrow(() -> new UserNotFoundException(userId));

		// 현재 비밀번호 확인
		if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
			return "현재 비밀번호가 일치하지 않습니다.";
		}

		// 새로운 비밀번호 암호화
		String encryptedNewPassword = passwordEncoder.encode(request.newPassword());

		// 비밀번호 업데이트
		user.modifyPassword(encryptedNewPassword);

		return "비밀번호가 성공적으로 변경되었습니다.";
	}

	@Override
	public void withdrawUser(Long userId) {
		User user = userRepository.getUser(userId).orElseThrow(() -> new UserNotFoundException(userId));
		Status status = statusRepository.findOrCreateStatus("회원", "탈퇴");
		user.withdrawUser(status);
	}
}
