package com.eventorback.usergrade.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.usergrade.domain.dto.UserGradeDto;
import com.eventorback.usergrade.domain.entity.UserGrade;
import com.eventorback.usergrade.exception.UserGradeAlreadyExistsException;
import com.eventorback.usergrade.exception.UserGradeNotFoundException;
import com.eventorback.usergrade.repository.UserGradeRepository;
import com.eventorback.usergrade.service.UserGradeService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserGradeServiceImpl implements UserGradeService {
	private final UserGradeRepository userGradeRepository;

	@Override
	public List<UserGradeDto> getUserGrades() {
		return userGradeRepository.findAll().stream().map(UserGradeDto::fromEntity).toList();
	}

	@Override
	public Page<UserGradeDto> getUserGrades(Pageable pageable) {
		return null;
	}

	@Override
	public UserGradeDto getUserGrade(Long userGradeId) {
		return userGradeRepository.findById(userGradeId)
			.map(UserGradeDto::fromEntity)
			.orElseThrow(() -> new UserGradeNotFoundException(userGradeId));
	}

	@Override
	public void createUserGrade(UserGradeDto request) {
		if (userGradeRepository.existsByName(request.name())) {
			throw new UserGradeAlreadyExistsException(request.name());
		}
		userGradeRepository.save(UserGrade.toEntity(request));
	}

	@Override
	public void updateUserGrade(Long userGradeId, UserGradeDto request) {
		UserGrade userGrade = userGradeRepository.findById(userGradeId)
			.orElseThrow(() -> new UserGradeNotFoundException(userGradeId));
		userGrade.updateUserGrade(request.name(), request.minAmount(), request.maxAmount(), request.pointRate());
	}

	@Override
	public void deleteUserGrade(Long userGradeId) {
		userGradeRepository.deleteById(userGradeId);
	}
}
