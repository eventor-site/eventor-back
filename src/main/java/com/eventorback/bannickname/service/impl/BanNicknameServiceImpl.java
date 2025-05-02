package com.eventorback.bannickname.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.bannickname.domain.dto.BanNicknameDto;
import com.eventorback.bannickname.domain.entity.BanNickname;
import com.eventorback.bannickname.exception.BanNicknameAlreadyExistsException;
import com.eventorback.bannickname.repository.BanNicknameRepository;
import com.eventorback.bannickname.service.BanNicknameService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BanNicknameServiceImpl implements BanNicknameService {
	private final BanNicknameRepository banNicknameRepository;

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "cache", key = "'banNicknames'", cacheManager = "cacheManager")
	public List<String> getCacheBanNicknames() {
		return banNicknameRepository.getCacheBanNicknames();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BanNicknameDto> getBanNicknames() {
		return banNicknameRepository.getBanNicknames();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<BanNicknameDto> getBanNicknames(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return banNicknameRepository.getBanNicknames(PageRequest.of(page, pageSize));
		//test
	}

	@Override
	@CacheEvict(cacheNames = "cache", key = "'banNicknames'", cacheManager = "cacheManager")
	public void createBanNickname(BanNicknameDto request) {
		if (banNicknameRepository.existsByNickname(request.nickname())) {
			throw new BanNicknameAlreadyExistsException();
		}
		banNicknameRepository.save(BanNickname.toEntity(request));
	}

	@Override
	@CacheEvict(cacheNames = "cache", key = "'banNicknames'", cacheManager = "cacheManager")
	public void deleteBanNickname(Long banNicknameId) {
		banNicknameRepository.deleteById(banNicknameId);
	}
}
