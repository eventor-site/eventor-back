package com.eventorback.user.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.global.util.NumberUtil;
import com.eventorback.grade.domain.entity.Grade;
import com.eventorback.grade.exception.GradeNotFoundException;
import com.eventorback.grade.repository.GradeRepository;
import com.eventorback.mail.service.MailService;
import com.eventorback.role.domain.entity.Role;
import com.eventorback.role.exception.RoleNotFoundException;
import com.eventorback.role.repository.RoleRepository;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.exception.StatusNotFoundException;
import com.eventorback.status.repository.StatusRepository;
import com.eventorback.user.domain.dto.CurrentUserDto;
import com.eventorback.user.domain.dto.request.CheckIdentifierRequest;
import com.eventorback.user.domain.dto.request.CheckNicknameRequest;
import com.eventorback.user.domain.dto.request.ModifyPasswordRequest;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLastLoginTimeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserAttributeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserRequest;
import com.eventorback.user.domain.dto.response.GetUserByIdentifier;
import com.eventorback.user.domain.dto.response.GetUserByUserId;
import com.eventorback.user.domain.dto.response.GetUserListResponse;
import com.eventorback.user.domain.dto.response.GetUserResponse;
import com.eventorback.user.domain.dto.response.GetUserTokenInfo;
import com.eventorback.user.domain.dto.response.OauthDto;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.exception.NicknameChangeCooldownBadRequestException;
import com.eventorback.user.exception.UserNotFoundException;
import com.eventorback.user.repository.UserRepository;
import com.eventorback.user.service.UserService;
import com.eventorback.userrole.domain.entity.UserRole;
import com.eventorback.userrole.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final GradeRepository gradeRepository;
	private final RoleRepository roleRepository;
	private final UserRoleRepository userRoleRepository;
	private final StatusRepository statusRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;

	@Override
	public Page<GetUserListResponse> getUsers(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return userRepository.getUsers(PageRequest.of(page, pageSize));
	}

	@Override
	public List<GetUserByIdentifier> searchUserByIdentifier(String keyword) {
		return userRepository.searchUserByIdentifier(keyword);
	}

	@Override
	public List<GetUserByUserId> searchUserByUserId(Long userId) {
		return userRepository.searchUserByUserId(userId);
	}

	@Override
	public GetUserTokenInfo getUserTokenInfoByIdentifier(String identifier) {
		User user = userRepository.findByIdentifier(identifier)
			.orElse(null);
		if (user == null) {
			return null;
		}

		List<String> userRoleNames = userRoleRepository.findAllByUserUserId(user.getUserId())
			.stream().map(userRole -> userRole.getRole().getName()).toList();

		return GetUserTokenInfo.fromEntity(user, userRoleNames);
	}

	@Override
	public GetUserTokenInfo getUserInfoByOauth(OauthDto request) {
		return userRepository.getUserInfoByOauth(request);
	}

	@Override
	public Boolean existsByOauth(OauthDto request) {
		return userRepository.existsByOauthIdAndOauthType(request.oauthId(), request.oauthType());
	}

	@Override
	public GetUserResponse getUserInfo(Long userId) {
		return userRepository.getUserInfo(userId).orElseThrow(UserNotFoundException::new);
	}

	@Override
	public void updateUser(Long userId, UpdateUserRequest request) {
		User user = userRepository.getUser(userId).orElseThrow(UserNotFoundException::new);

		if (!user.getNickname().equals(request.nickname())) {

			if (user.getLastNicknameChangeTime() == null) {
				user.updateLastNicknameChangeTime();
			} else {
				LocalDateTime currentTime = LocalDateTime.now();

				// 날짜 차이 계산
				long monthsBetween = ChronoUnit.MONTHS.between(user.getLastNicknameChangeTime(), currentTime);

				if (monthsBetween >= 1) {
					user.updateLastNicknameChangeTime();
				} else {
					throw new NicknameChangeCooldownBadRequestException();
				}
			}
		}

		user.updateUser(request);
	}

	@Override
	public void updateUserAttributeByAdmin(Long userId, UpdateUserAttributeRequest request) {
		User user = userRepository.getUser(userId).orElseThrow(UserNotFoundException::new);
		Status status = statusRepository.getStatus(request.statusId()).orElseThrow(StatusNotFoundException::new);
		Grade grade = gradeRepository.findById(request.gradeId()).orElseThrow(GradeNotFoundException::new);

		user.updateAttribute(status, grade);
	}

	@Override
	public void withdrawUser(Long userId) {
		User user = userRepository.getUser(userId).orElseThrow(UserNotFoundException::new);
		Status status = statusRepository.findOrCreateStatus("회원", "탈퇴");
		user.updateStatus(status);
	}

	@Override
	public Boolean meCheckRoles(CurrentUserDto currentUser, String roleName) {
		return currentUser != null && (currentUser.roles().contains(roleName));
	}

	@Override
	public List<String> meRoles(CurrentUserDto currentUser) {
		return currentUser != null ? currentUser.roles() : new ArrayList<>();
	}

	@Override
	public String meCheckNickname(Long userId, CheckNicknameRequest request) {
		String nickname = request.nickname();

		if (nickname.contains("[EM]")) {
			return "사용할 수 없는 아이디 입니다.";
		}

		if (userId != null && userRepository.existsByUserIdNotAndNickname(userId, nickname)) {
			return "이미 존재하는 닉네임 입니다.";
		}

		return "사용 가능한 닉네임 입니다.";
	}

	@Override
	public void updateLastLoginTime(UpdateLastLoginTimeRequest request) {
		User user = userRepository.getUser(request.userId()).orElseThrow(UserNotFoundException::new);
		user.updateLastLoginTime(request);
	}

	@Override
	public String modifyPassword(Long userId, ModifyPasswordRequest request) {
		User user = userRepository.getUser(userId).orElseThrow(UserNotFoundException::new);

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
	public void signup(SignUpRequest request) {
		Status status = statusRepository.findOrCreateStatus("회원", "활성");
		Grade grade = gradeRepository.findByName("1").orElseThrow(StatusNotFoundException::new);

		String encodedPassword = request.password() != null ? passwordEncoder.encode(request.password()) : null;
		User user = userRepository.save(User.toEntity(status, grade, request, encodedPassword));

		// 회원 가입시 기본 권한 데이터 설정
		Role role = roleRepository.findByName("member").orElseThrow(RoleNotFoundException::new);
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
	public String checkNickname(CheckNicknameRequest request) {
		String identifier = request.nickname();

		if (identifier.contains("[EM]")) {
			return "사용할 수 없는 아이디 입니다.";
		}

		if (userRepository.existsByNickname(request.nickname())) {
			return "이미 존재하는 닉네임 입니다.";
		}

		return "사용 가능한 닉네임 입니다.";
	}

	@Override
	public String recoverIdentifier(String email) {
		User user = userRepository.findByEmail(email).orElse(null);
		if (user != null) {
			// mailService.sendMail(email, "아이디 찾기", user.getIdentifier());
			// return email + "로 아이디를 전송하였습니다.";
			return "가입된 아이디 입니다.";
		}
		return "가입된 아이디가 아닙니다.";
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
			return user.getEmail() + "로 새로운 비밀번호가 전송되었습니다.";
		}
		return "가입된 아이디가 아닙니다.";
	}
}
