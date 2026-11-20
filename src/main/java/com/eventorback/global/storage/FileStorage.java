package com.eventorback.global.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.eventorback.global.config.EventorProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * 업로드 파일 저장소(docs/migration.md 4절).
 * <ul>
 *     <li>저장 루트: {@code eventor.storage.root}(기본 ./uploads). 로컬·운영 같은 설정이고 운영은 이 폴더를 마운트한다</li>
 *     <li>공개 URL: {@code /uploads/{상대경로}}. DB에는 이 상대 URL만 저장한다(도메인 없음)</li>
 *     <li>운영에서는 nginx가 /uploads/를 직접 서빙하고, 로컬에서는 {@code UploadsController}가 서빙한다</li>
 * </ul>
 */
@Slf4j
@Component
public class FileStorage {
	public static final String URL_PREFIX = "/uploads/";

	private final Path root;

	public FileStorage(EventorProperties properties) {
		this.root = Paths.get(properties.storage().root()).toAbsolutePath().normalize();
		log.info("업로드 저장 루트: {}", root);
	}

	public Path root() {
		return root;
	}

	/** 상대 경로(예: postimage/20261119/a.webp)에 저장하고 공개 URL(/uploads/postimage/20261119/a.webp)을 돌려준다 */
	public String save(String relativePath, byte[] bytes) throws IOException {
		Path target = resolveRelative(relativePath)
			.orElseThrow(() -> new IOException("저장 경로가 올바르지 않습니다: " + relativePath));
		Files.createDirectories(target.getParent());
		Files.write(target, bytes);
		return URL_PREFIX + root.relativize(target).toString().replace('\\', '/');
	}

	/**
	 * DB에 저장된 URL을 실제 파일 경로로 바꾼다.
	 * 새 형식(/uploads/...)과 마이그레이션 전 절대 주소(https://도메인/postimage/...)를 모두 받는다.
	 */
	public Optional<Path> resolveUrl(String url) {
		if (url == null) {
			return Optional.empty();
		}
		if (url.startsWith(URL_PREFIX)) {
			return resolveRelative(url.substring(URL_PREFIX.length()));
		}
		int legacy = url.indexOf("/postimage/");
		if (url.startsWith("http") && legacy >= 0) {
			return resolveRelative(url.substring(legacy + 1));
		}
		return Optional.empty();
	}

	public boolean delete(String url) {
		return resolveUrl(url).map(path -> {
			try {
				return Files.deleteIfExists(path);
			} catch (IOException e) {
				log.warn("파일 삭제 실패: {} - {}", path, e.getMessage());
				return false;
			}
		}).orElse(false);
	}

	/** 루트 밖으로 나가는 경로(../ 등)는 거부한다 */
	public Optional<Path> resolveRelative(String relativePath) {
		if (relativePath == null || relativePath.isBlank()) {
			return Optional.empty();
		}
		Path resolved = root.resolve(relativePath).normalize();
		return resolved.startsWith(root) && !resolved.equals(root) ? Optional.of(resolved) : Optional.empty();
	}
}
