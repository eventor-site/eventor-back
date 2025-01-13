package com.eventorback.userstop.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.reporttype.domain.entity.ReportType;
import com.eventorback.reporttype.exception.ReportTypeNotFoundException;
import com.eventorback.reporttype.repository.ReportTypeRepository;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.repository.StatusRepository;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.repository.UserRepository;
import com.eventorback.userstop.domain.dto.UserStopDto;
import com.eventorback.userstop.domain.dto.response.GetUserStopResponse;
import com.eventorback.userstop.domain.entity.UserStop;
import com.eventorback.userstop.exception.UserStopNotFoundException;
import com.eventorback.userstop.repository.UserStopRepository;
import com.eventorback.userstop.service.UserStopService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserStopServiceImpl implements UserStopService {
	private final UserStopRepository userStopRepository;
	private final UserRepository userRepository;
	private final ReportTypeRepository reportTypeRepository;
	private final StatusRepository statusRepository;

	@Override
	public List<GetUserStopResponse> getUserStops() {
		return userStopRepository.findAll().stream().map(GetUserStopResponse::fromEntity).toList();
	}

	@Override
	public Page<UserStopDto> getUserStops(Pageable pageable) {
		return null;
	}

	@Override
	public UserStopDto getUserStop(Long userStopId) {
		UserStop userStop = userStopRepository.findById(userStopId)
			.orElseThrow(() -> new UserStopNotFoundException(userStopId));
		return UserStopDto.fromEntity(userStop);
	}

	@Override
	public void createUserStop(UserStopDto request) {
		Status status = statusRepository.findOrCreateStatus("회원", "정지");
		User user = userRepository.findByIdentifier(request.identifier())
			.orElseThrow(() -> new UserStopNotFoundException(request.identifier()));

		user.updateStatus(status);

		ReportType reportType = reportTypeRepository.findById(request.reportTypeId())
			.orElseThrow(() -> new ReportTypeNotFoundException(request.reportTypeId()));

		// 시작 시간 설정 (현재 시간)
		LocalDateTime startTime = LocalDateTime.now();

		// 종료 시간 계산 (시작 시간 + 신고 유형에 저장된 일수)
		LocalDateTime endTime = startTime.plusDays(reportType.getDay());

		userStopRepository.save(UserStop.toEntity(user, reportType, startTime, endTime));
	}

	@Override
	public void deleteUserStop(Long userStopId) {
		UserStop userStop = userStopRepository.findById(userStopId)
			.orElseThrow(() -> new UserStopNotFoundException(userStopId));
		Status status = statusRepository.findOrCreateStatus("회원", "활성");

		userStop.getUser().updateStatus(status);

		userStopRepository.delete(userStop);
	}

}
