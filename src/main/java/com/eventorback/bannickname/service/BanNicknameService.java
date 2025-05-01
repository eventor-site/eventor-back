package com.eventorback.bannickname.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.bannickname.domain.dto.BanNicknameDto;

public interface BanNicknameService {

	List<String> getCacheBanNicknames();

	List<BanNicknameDto> getBanNicknames();

	Page<BanNicknameDto> getBanNicknames(Pageable pageable);

	void createBanNickname(BanNicknameDto request);

	void deleteBanNickname(Long statusId);
}
