package com.eventorback.auth.domain;

import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리프레시 토큰(V2 마이그레이션). 토큰 원문은 쿠키에만 있고 DB에는 SHA-256 해시만 둔다.
 */
@Getter
@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "refresh_token_id")
	private Long refreshTokenId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "token_hash", nullable = false, length = 64, columnDefinition = "char(64)")
	private String tokenHash;

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	/** 재발급으로 교체된 시각. null이면 아직 쓰는 토큰 */
	@Column(name = "rotated_at")
	private LocalDateTime rotatedAt;

	public RefreshToken(Long userId, String tokenHash, LocalDateTime expiresAt) {
		this.userId = userId;
		this.tokenHash = tokenHash;
		this.expiresAt = expiresAt;
		this.createdAt = LocalDateTime.now();
	}

	public boolean isExpired(LocalDateTime now) {
		return !expiresAt.isAfter(now);
	}

	public boolean isRotated() {
		return rotatedAt != null;
	}

	/** 교체된 지 grace 이내인지(동시에 보낸 요청들이 같은 토큰으로 재발급을 시도하는 경우) */
	public boolean isWithinGrace(LocalDateTime now, Duration grace) {
		return rotatedAt != null && rotatedAt.plus(grace).isAfter(now);
	}

	public void markRotated(LocalDateTime now) {
		this.rotatedAt = now;
	}
}
