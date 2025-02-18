package com.eventorback.image.service;

import java.nio.file.Path;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.eventorback.image.exception.FileUploadException;

/**
 * @author 이경헌
 * 이미지 관련 서비스 인터페이스입니다.
 */
public interface ImageService {

	void upload(List<MultipartFile> files, String folderName, Long postId) throws FileUploadException;

	String saveFile(Path folderPath, String fileName, MultipartFile file);

	void createDirectoryIfNotExists(Path folderPath);

	String getFileExtension(String fileName);

	void checkFileExtension(String fileContentType);

	void createImage(Long postId, String originalName, String newName, String url, Long size);

	void deleteImage(Long postId, List<Long> deleteImageIds);
}
