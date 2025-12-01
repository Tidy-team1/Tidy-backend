package com.tidy.tidy.domain.feedback;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findBySlideId(Long slideId);

    List<Feedback> findBySlidePresentationId(Long presentationId);

    List<Feedback> findBySlideIndex(Integer slideIndex);
}
