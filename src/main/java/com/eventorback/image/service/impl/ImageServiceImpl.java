package com.eventorback.image.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eventorback.image.domain.entity.Image;
import com.eventorback.image.exception.FileExtensionException;
import com.eventorback.image.exception.ImageNotFoundException;
import com.eventorback.image.repository.ImageRepository;
import com.eventorback.image.service.ImageService;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.post.exception.PostNotFoundException;
import com.eventorback.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
	private final ImageRepository imageRepository;
	private final PostRepository postRepository;

	private static final String DOMAIN_URL = "https://www.eventor.store/";

	@Value("${upload.path}")
	private String uploadPath;

	@Override
	// @Async("imageUploadExecutor")
	public void upload(List<MultipartFile> files, String folderName, Long postId) {
		for (MultipartFile file : files) {
			// 1. 날짜별 하위 폴더 생성 (YYYYMMDD 형식)
			String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
			Path folderPath = Paths.get(uploadPath, folderName, today);
			createDirectoryIfNotExists(folderPath);

			// 2. 파일 이름 생성 (UUID 로 중복 방지)
			String originalFilename = file.getOriginalFilename();
			String fileExtension = getFileExtension(originalFilename);
			String newFileName = postId.toString() + "_" + UUID.randomUUID() + fileExtension;

			// 3. 파일 확장자 검사
			checkFileExtension(fileExtension);

			// 4. 파일 저장
			saveFile(folderPath, newFileName, file);

			//임시 백엔드 리소스 URL
			String url = DOMAIN_URL + folderName + "/" + today + "/" + newFileName;

			// 5. DB에 이미지 정보 저장
			createImage(postId, originalFilename, newFileName, url, file.getSize());
		}
	}

	@Override
	public String saveFile(Path folderPath, String fileName, MultipartFile file) {
		Path filePath = folderPath.resolve(fileName);
		try {
			Files.write(filePath, file.getBytes());
		} catch (IOException e) {
			throw new RuntimeException("파일 저장 실패: " + e.getMessage(), e);
		}
		return filePath.toString();
	}

	/**
	 * 경로가 없으면 생성
	 */
	@Override
	public void createDirectoryIfNotExists(Path folderPath) {
		if (!Files.exists(folderPath)) {
			try {
				Files.createDirectories(folderPath);
			} catch (IOException e) {
				throw new RuntimeException("폴더 생성 실패: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * 파일 확장자 추출
	 */
	@Override
	public String getFileExtension(String fileName) {
		if (fileName != null && fileName.contains(".")) {
			return fileName.substring(fileName.lastIndexOf("."));
		}
		return "";
	}

	@Override
	public void checkFileExtension(String fileContentType) {
		String[] imageExtensions = {"jpg", "jpeg", "png", "gif"};

		for (String extension : imageExtensions) {
			if (fileContentType.toLowerCase().endsWith(extension)) {
				return;
			}
		}
		throw new FileExtensionException();
	}

	@Override
	public void createImage(Long postId, String originalName, String newName, String url, Long size) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
		imageRepository.save(new Image(post, originalName, newName, url, size));
	}

	@Override
	public void deleteImage(Long postId, List<Long> deleteImageIds) {
		if (deleteImageIds != null) {
			deleteImageIds.forEach(imageId -> {
				// 1. DB 에서 이미지 정보 조회
				Image image = imageRepository.findById(imageId)
					.orElseThrow(ImageNotFoundException::new);

				// 2. 실제 파일 경로 가져오기
				Path filePath = Paths.get(uploadPath,
					image.getUrl().replaceFirst(DOMAIN_URL, ""));

				try {
					// 3. 실제 파일 삭제
					Files.deleteIfExists(filePath);
				} catch (IOException e) {
					log.error("파일 삭제 실패: {}", filePath, e);
				}

				// 4. DB 에서 이미지 정보 삭제
				imageRepository.deleteById(imageId);
			});
		}

	}
}
