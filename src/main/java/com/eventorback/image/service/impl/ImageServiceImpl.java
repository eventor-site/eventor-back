package com.eventorback.image.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.eventorback.category.service.CategoryService;
import com.eventorback.image.domain.dto.request.DeleteImageRequest;
import com.eventorback.image.domain.dto.response.GetImageResponse;
import com.eventorback.image.domain.entity.Image;
import com.eventorback.image.exception.CreateFolderException;
import com.eventorback.image.exception.FileExtensionException;
import com.eventorback.image.exception.FileExtentionNotFoundException;
import com.eventorback.image.exception.FileSaveException;
import com.eventorback.image.exception.ImageCapacityExceededException;
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
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
	private final ImageRepository imageRepository;
	private final PostRepository postRepository;
	private final CategoryService categoryService;
	private final Long MAX_IMAGE_SIZE = 10L * 1024 * 1024;
	private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".jfif",
		".webp");
	private static final List<String> VIDEO_EXTENSIONS = Arrays.asList(".mp4", ".mov", ".avi", ".wmv", ".mkv", ".webm");

	@Value("${upload.domainUrl}")
	private String domainUrl;

	@Value("${upload.path}")
	private String uploadPath;

	@Override
	// @Async("imageUploadExecutor")
	public List<GetImageResponse> upload(MultipartFile file, String folderName, Long postId, String categoryName,
		boolean isThumbnail, boolean isPasted) {

		// 날짜별 하위 폴더 생성 (YYYYMMDD 형식)
		String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
		Path folderPath = Paths.get(uploadPath, folderName, today);
		createDirectoryIfNotExists(folderPath);

		// 파일 이름 생성 (UUID 로 중복 방지)
		String originalFilename = file.getOriginalFilename();
		String fileExtension = getFileExtension(originalFilename);
		String newFileName = postId.toString() + "_" + UUID.randomUUID() + fileExtension;

		// 파일 확장자 검사
		checkFileExtension(fileExtension);

		// 파일 용량 검사
		Long totalFileSize = imageRepository.sumSizeByPostPostId(postId);
		if (file.getSize() > MAX_IMAGE_SIZE || file.getSize() + totalFileSize > MAX_IMAGE_SIZE) {
			throw new ImageCapacityExceededException();
		}

		// 4. 파일 저장
		saveFile(folderPath, newFileName, file);

		// 리소스 URL
		String url = domainUrl + "/" + folderName + "/" + today + "/" + newFileName;

		// 기존 썸네일 존재하면 제거
		if (isThumbnail) {
			imageRepository.deleteByPostPostIdAndIsThumbnail(postId, true);
		}

		if (!categoryService.getCategoryNames("이벤트").contains(categoryName)
			&& !imageRepository.existsByPostPostIdAndIsThumbnail(postId, true)) {
			isThumbnail = true;
		}

		// 5. DB에 이미지 정보 저장
		createImage(postId, originalFilename, newFileName, url, fileExtension, file.getSize(), isThumbnail, isPasted);

		return imageRepository.getAllByPostId(postId);
	}

	@Override
	public void saveFile(Path folderPath, String fileName, MultipartFile file) {
		Path filePath = folderPath.resolve(fileName);
		try {
			Files.write(filePath, file.getBytes());
		} catch (IOException e) {
			throw new FileSaveException(e.getMessage());
		}
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
				throw new CreateFolderException(e.getMessage());
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
		throw new FileExtentionNotFoundException();
	}

	@Override
	public void checkFileExtension(String fileContentType) {
		for (String extension : IMAGE_EXTENSIONS) {
			if (fileContentType.toLowerCase().endsWith(extension)) {
				return;
			}
		}

		for (String extension : VIDEO_EXTENSIONS) {
			if (fileContentType.toLowerCase().endsWith(extension)) {
				return;
			}
		}
		throw new FileExtensionException();
	}

	@Override
	public void createImage(Long postId, String originalName, String newName, String url, String fileExtension,
		Long size,
		boolean isThumbnail, boolean isPasted) {
		Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
		imageRepository.save(
			new Image(post, originalName, newName, url, fileExtension, determineFileType(fileExtension), size,
				isThumbnail, isPasted));
	}

	@Override
	public List<GetImageResponse> deleteImage(DeleteImageRequest request) {
		request.imageIds().forEach(imageId -> {
			// 1. DB 에서 이미지 정보 조회
			Image image = imageRepository.findById(imageId)
				.orElseThrow(ImageNotFoundException::new);

			// 2. 실제 파일 경로 가져오기
			Path filePath = Paths.get(uploadPath,
				image.getUrl().replaceFirst(domainUrl, ""));

			try {
				// 3. 실제 파일 삭제
				Files.deleteIfExists(filePath);
			} catch (IOException e) {
				log.error("파일 삭제 실패: {}", filePath, e);
			}

			// 4. DB 에서 이미지 정보 삭제
			imageRepository.deleteById(imageId);

		});

		List<GetImageResponse> images = imageRepository.getAllByPostId(request.postId());

		if (!categoryService.getCategoryNames("이벤트").contains(request.categoryName())
			&& !imageRepository.existsByPostPostIdAndIsThumbnail(request.postId(), true) && !images.isEmpty()) {
			Image image = imageRepository.findById(images.getFirst().imageId())
				.orElseThrow(ImageNotFoundException::new);
			image.setThumbnail();
		}

		return images;
	}

	@Override
	public void deleteTempImage(Long userId) {
		List<Image> images = imageRepository.findByPostUserUserIdAndPostStatusName(userId, "작성중");

		images.forEach(image -> {
			// 2. 실제 파일 경로 가져오기
			Path filePath = Paths.get(uploadPath,
				image.getUrl().replaceFirst(domainUrl, ""));

			try {
				// 3. 실제 파일 삭제
				Files.deleteIfExists(filePath);
			} catch (IOException e) {
				log.error("파일 삭제 실패: {}", filePath, e);
			}

			// 4. DB 에서 이미지 정보 삭제
			imageRepository.deleteById(image.getImageId());
		});
	}

	@Override
	public String determineFileType(String extension) {
		if (IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
			return "image";
		} else if (VIDEO_EXTENSIONS.contains(extension.toLowerCase())) {
			return "video";
		}
		return null; // 알 수 없는 타입
	}

}
