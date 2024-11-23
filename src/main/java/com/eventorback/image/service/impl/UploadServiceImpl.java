package com.eventorback.image.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eventorback.image.exception.FileUploadException;
import com.eventorback.image.service.ImageService;
import com.eventorback.image.service.UploadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {
	private final ImageService imageService;

	@Override
	public void uploads(List<MultipartFile> files, String folderName, Long postId) throws FileUploadException {
		for (MultipartFile file : files) {
			imageService.upload(file, folderName, postId);
		}
	}
}
