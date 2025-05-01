package com.eventorback.bannickname.domain.entity;

import com.eventorback.bannickname.domain.dto.BanNicknameDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "ban_nicknames")
public class BanNickname {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ban_nickname_id")
	private Long banNicknameId;

	@Column(name = "nickname")
	private String nickname;

	@Builder
	public BanNickname(String nickname) {
		this.nickname = nickname;
	}

	public static BanNickname toEntity(BanNicknameDto request) {
		return BanNickname.builder()
			.nickname(request.nickname())
			.build();
	}

}
