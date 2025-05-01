package com.eventorback.bannickname.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.bannickname.domain.dto.BanNicknameDto;

public interface CustomBanNicknameRepository {

	List<String> getCacheBanNicknames();

	List<BanNicknameDto> getBanNicknames();

	Page<BanNicknameDto> getBanNicknames(Pageable pageable);
}
