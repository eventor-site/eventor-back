package com.eventorback.global.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.global.config.EventorProperties;

import jakarta.servlet.http.HttpServletRequest;

/**
 * /uploads/** 파일 응답. 운영에서는 nginx가 같은 폴더를 직접 서빙하므로 여기까지 오지 않는다(로컬 개발용).
 * 로컬에 파일이 없고 {@code eventor.storage.fallback-base-url}이 있으면 그 사이트의 같은 이미지로 302 보낸다
 * (로컬 DB가 운영 복제본이라 이미지 파일은 운영 서버에만 있다).
 */
@RestController
public class UploadsController {
	private final FileStorage fileStorage;
	private final String fallbackBaseUrl;

	public UploadsController(FileStorage fileStorage, EventorProperties properties) {
		this.fileStorage = fileStorage;
		String fallback = properties.storage().fallbackBaseUrl();
		this.fallbackBaseUrl = fallback == null || fallback.isBlank() ? null : fallback.replaceAll("/+$", "");
	}

	@GetMapping("/uploads/**")
	public ResponseEntity<Resource> get(HttpServletRequest request) throws IOException {
		String url = request.getRequestURI();
		Optional<Path> path = fileStorage.resolveUrl(url);

		if (path.isPresent() && Files.isRegularFile(path.get())) {
			Resource resource = new FileSystemResource(path.get());
			MediaType type = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
			return ResponseEntity.ok()
				.contentType(type)
				.cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
				.body(resource);
		}

		if (fallbackBaseUrl != null && path.isPresent()) {
			// 운영은 컷오버 전 /postimage/..., 후 /uploads/postimage/... 이고 nginx가 옛 주소를 새 주소로 넘긴다.
			// 그래서 옛 주소로 보내면 컷오버 전후 모두 열린다.
			String legacyPath = url.substring(FileStorage.URL_PREFIX.length() - 1);
			return ResponseEntity.status(HttpStatus.FOUND)
				.header(HttpHeaders.LOCATION, fallbackBaseUrl + legacyPath)
				.build();
		}
		return ResponseEntity.notFound().build();
	}
}
