package com.eventorback.usergrade.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sikyeojoback.usergrade.domain.dto.UserGradeDto;

public interface UserGradeService {
	List<UserGradeDto> getUserGrades();

	Page<UserGradeDto> getUserGrades(Pageable pageable);

	UserGradeDto getUserGrade(Long getUserGradeId);

	void createUserGrade(UserGradeDto request);

	void updateUserGrade(Long userGradeId, UserGradeDto request);

	void deleteUserGrade(Long userGradeId);
}
