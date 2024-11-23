package com.eventorback.image.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.eventorback.image.exception.FileUploadException;

public interface UploadService {

	void uploads(List<MultipartFile> files, String folderName, Long postId) throws FileUploadException;
}
