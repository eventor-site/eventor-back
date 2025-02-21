package com.eventorback.userstop.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import com.eventorback.userstop.domain.dto.response.GetUserStopByUserIdResponse;
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
	public Page<GetUserStopResponse> getUserStops(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return userStopRepository.getUserStops(PageRequest.of(page, pageSize));
	}

	@Override
	public UserStopDto getUserStop(Long userStopId) {
		UserStop userStop = userStopRepository.findById(userStopId)
			.orElseThrow(UserStopNotFoundException::new);
		return UserStopDto.fromEntity(userStop);
	}

	@Override
	public List<GetUserStopByUserIdResponse> getUserStopsByUserId(Long userId) {
		return userStopRepository.getUserStopByUserId(userId);
	}

	@Override
	public void createUserStop(UserStopDto request) {
		Status status = statusRepository.findOrCreateStatus("회원", "정지");
		User user = userRepository.findByUserId(request.userId())
			.orElseThrow(UserStopNotFoundException::new);

		user.updateStatus(status);

		ReportType reportType = reportTypeRepository.findById(request.reportTypeId())
			.orElseThrow(ReportTypeNotFoundException::new);

		userStopRepository.save(UserStop.toEntity(user, reportType, request));
	}

	@Override
	public void deleteUserStop(Long userStopId) {
		UserStop userStop = userStopRepository.findById(userStopId)
			.orElseThrow(UserStopNotFoundException::new);
		Status status = statusRepository.findOrCreateStatus("회원", "활성");

		userStop.getUser().updateStatus(status);

		userStopRepository.delete(userStop);
	}

}
