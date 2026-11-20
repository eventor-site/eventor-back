package com.eventorback.global;

import static org.assertj.core.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.eventorback.global.config.EventorProperties;
import com.eventorback.global.storage.FileStorage;

class FileStorageTest {

	@TempDir
	Path temp;

	private FileStorage storage() {
		return new FileStorage(new EventorProperties(
			new EventorProperties.Jwt("x".repeat(32), Duration.ofMinutes(10), Duration.ofDays(1)),
			new EventorProperties.Cookie(false),
			new EventorProperties.Google("id", "secret"),
			new EventorProperties.Storage(temp.toString(), ""),
			new EventorProperties.Gemini(null, "a", "b", Duration.ofSeconds(60), false, java.util.Map.of())));
	}

	@Test
	void 저장하면_도메인없는_uploads_주소를_돌려준다() throws Exception {
		FileStorage storage = storage();
		String url = storage.save("postimage/20261119/a.webp", new byte[] {1, 2});

		assertThat(url).isEqualTo("/uploads/postimage/20261119/a.webp");
		assertThat(Files.readAllBytes(temp.resolve("postimage/20261119/a.webp"))).containsExactly(1, 2);
		assertThat(storage.resolveUrl(url)).contains(temp.resolve("postimage/20261119/a.webp"));
	}

	@Test
	void 마이그레이션_전_절대주소도_파일경로로_바꾼다() {
		assertThat(storage().resolveUrl("https://eventor.duckdns.org/postimage/20250101/b.png"))
			.contains(temp.resolve("postimage/20250101/b.png"));
	}

	@Test
	void 저장루트_밖으로_나가는_경로는_거부한다() {
		FileStorage storage = storage();
		assertThat(storage.resolveRelative("../etc/passwd")).isEmpty();
		assertThat(storage.resolveUrl("/uploads/../../etc/passwd")).isEmpty();
		assertThatThrownBy(() -> storage.save("../x.webp", new byte[] {1})).isInstanceOf(java.io.IOException.class);
	}
}
