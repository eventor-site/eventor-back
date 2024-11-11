package com.eventorback.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sikyeojoback.favorite.domain.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}