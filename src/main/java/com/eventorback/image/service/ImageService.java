package com.eventorback.image.service;

import java.nio.file.Path;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.eventorback.image.domain.dto.request.DeleteImageRequest;
import com.eventorback.image.domain.dto.response.GetImageResponse;
import com.eventorback.image.exception.FileUploadException;

/**
 * @author 이경헌
 * 이미지 관련 서비스 인터페이스입니다.
 */
public interface ImageService {

	List<GetImageResponse> upload(MultipartFile file, String folderName, Long postId, String categoryName,
		boolean isThumbnail, boolean isPasted) throws FileUploadException;

	void saveFile(Path folderPath, String fileName, MultipartFile file);

	void createDirectoryIfNotExists(Path folderPath);

	String getFileExtension(String fileName);

	void checkFileExtension(String fileContentType);

	void createImage(Long postId, String originalName, String newName, String url, String fileExtension, Long size,
		boolean isThumbnail, boolean isPasted);

	List<GetImageResponse> deleteImage(DeleteImageRequest request);

	void deleteTempImage(Long userId);
}
