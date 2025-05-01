package com.eventorback.bannickname.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.bannickname.domain.entity.BanNickname;

public interface BanNicknameRepository extends JpaRepository<BanNickname, Long>, CustomBanNicknameRepository {

	boolean existsByNickname(String name);

}