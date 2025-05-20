package com.eventorback.image.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public class CustomMultipartFile implements MultipartFile {

	private final byte[] content;  // 파일 내용
	private final String originalFilename;  // 원본 파일명
	private final String contentType;  // 콘텐츠 타입

	// 생성자
	public CustomMultipartFile(InputStream inputStream, String originalFilename, String contentType) throws
		IOException {
		this.content = inputStream.readAllBytes();
		this.originalFilename = originalFilename;
		this.contentType = contentType;
	}

	@Override
	public String getName() {
		return originalFilename;
	}

	@Override
	public String getOriginalFilename() {
		return originalFilename;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public boolean isEmpty() {
		return content.length == 0;
	}

	@Override
	public long getSize() {
		return content.length;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return content;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(content);
	}

	@Override
	public void transferTo(File dest) {
	}
}