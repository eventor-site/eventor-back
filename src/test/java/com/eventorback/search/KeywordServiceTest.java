package com.eventorback.search;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.eventorback.search.service.KeywordServiceAccess;

class KeywordServiceTest {

	@Test
	void 검색_구간은_5분_단위로_자른다() {
		LocalDateTime t = LocalDateTime.of(2026, 11, 19, 14, 27, 31);
		assertThat(KeywordServiceAccess.bucket(t, 0)).isEqualTo(LocalDateTime.of(2026, 11, 19, 14, 25));
		assertThat(KeywordServiceAccess.bucket(t, -1)).isEqualTo(LocalDateTime.of(2026, 11, 19, 14, 20));
		assertThat(KeywordServiceAccess.bucket(LocalDateTime.of(2026, 11, 19, 0, 3), -1))
			.isEqualTo(LocalDateTime.of(2026, 11, 18, 23, 55));
	}
}
