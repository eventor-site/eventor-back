package com.eventorback.user.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.global.util.NumberUtil;
import com.eventorback.mail.service.MailService;
import com.eventorback.role.domain.entity.Role;
import com.eventorback.role.exception.RoleNotFoundException;
import com.eventorback.role.repository.RoleRepository;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.exception.StatusNotFoundException;
import com.eventorback.status.repository.StatusRepository;
import com.eventorback.user.domain.dto.request.CheckIdentifierRequest;
import com.eventorback.user.domain.dto.request.ModifyPasswordRequest;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLastLoginTimeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserRequest;
import com.eventorback.user.domain.dto.response.GetUserByAddShopResponse;
import com.eventorback.user.domain.dto.response.GetUserResponse;
import com.eventorback.user.domain.dto.response.UserTokenInfo;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.exception.UserAlreadyExistsException;
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
	private final MailService mailService;

	@Override
	public List<GetUserByAddShopResponse> searchUserById(String keyword) {
		return userRepository.searchUserById(keyword);
	}

	@Override
	public UserTokenInfo getUserTokenInfoByIdentifier(String identifier) {
		User user = userRepository.findByIdentifier(identifier)
			.orElse(null);
		if (user == null) {
			return null;
		}

		List<String> userRoleNames = userRoleRepository.findAllByUserUserId(user.getUserId())
			.stream().map(userRole -> userRole.getRole().getName()).toList();

		return UserTokenInfo.fromEntity(user, userRoleNames);
	}

	@Override
	public GetUserResponse getUserInfo(Long userId) {
		return userRepository.getUserInfo(userId).orElseThrow(() -> new UserNotFoundException(userId));
	}

	@Override
	public void signup(SignUpRequest request) {
		if (userRepository.existsByIdentifier(request.identifier())) {
			throw new UserAlreadyExistsException(request.identifier());
		}

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
	public String checkIdentifier(CheckIdentifierRequest request) {
		if (userRepository.existsByIdentifier(request.identifier())) {
			return "이미 존재하는 아이디 입니다.";
		}
		return "사용 가능한 아이디 입니다.";
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

	@Override
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public String recoverIdentifier(String email) {
		User user = userRepository.findByEmail(email).orElse(null);
		if (user != null) {
			mailService.sendMail(email, "아이디 찾기", user.getIdentifier());
			return email + "로 아이디를 전송하였습니다.";
		}
		return "가입된 아이디가 없습니다.";
	}

	@Override
	public String recoverPassword(String identifier) {
		User user = userRepository.findByIdentifier(identifier).orElse(null);
		if (user != null) {
			// 새로운 비밀번호
			String newPassword = NumberUtil.createRandom(8);
			String encryptedNewPassword = passwordEncoder.encode(newPassword);

			// 비밀번호 업데이트
			user.modifyPassword(encryptedNewPassword);

			mailService.sendMail(user.getEmail(), "비밀번호 찾기", newPassword);
			return "이메일로 새로운 비밀번호가 전송되었습니다.";
		}
		return "가입된 아이디가 아닙니다.";
	}
}
