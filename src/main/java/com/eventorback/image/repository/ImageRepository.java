package com.eventorback.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.image.domain.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}