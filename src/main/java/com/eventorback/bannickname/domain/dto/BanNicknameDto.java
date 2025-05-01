package com.eventorback.bannickname.domain.dto;

import com.eventorback.bannickname.domain.entity.BanNickname;

import lombok.Builder;

@Builder
public record BanNicknameDto(
	Long banNicknameId,
	String nickname) {

	public static BanNicknameDto fromEntity(BanNickname banNickname) {
		return BanNicknameDto.builder()
			.banNicknameId(banNickname.getBanNicknameId())
			.nickname(banNickname.getNickname())
			.build();
	}
}
