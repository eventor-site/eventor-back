package com.eventorback.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sikyeojoback.image.domain.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}