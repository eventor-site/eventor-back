package com.eventorback.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.event.domain.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}