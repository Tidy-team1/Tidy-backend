package com.tidy.tidy.domain.presentation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {
    List<Presentation> findBySpaceId(Long teamSpaceId);
}
