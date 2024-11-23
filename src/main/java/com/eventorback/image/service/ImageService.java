package com.eventorback.image.service;

import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

import com.eventorback.image.exception.FileUploadException;

/**
 * @author 이경헌
 * 파일 업로드 서비스 인터페이스입니다.
 */
public interface ImageService {

	void upload(MultipartFile file, String folderName, Long postId) throws FileUploadException;

	String saveFile(Path folderPath, String fileName, MultipartFile file);

	void createDirectoryIfNotExists(Path folderPath);

	String getFileExtension(String fileName);

	void checkFileExtension(String fileContentType);

	void createImage(Long postId, String originalName, String newName, String url);
}
