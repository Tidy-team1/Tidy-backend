package com.tidy.tidy.domain.slide;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlideRepository extends JpaRepository<Slide, Long> {
    List<Slide> findByPresentationIdOrderBySlideIndex(Long presentationId);
}
