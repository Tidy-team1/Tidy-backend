package com.tidy.tidy.domain.feedback;

import com.tidy.tidy.domain.presentation.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findBySlideId(Long slideId);

    List<Feedback> findBySlidePresentationId(Long presentationId);

    List<Feedback> findBySlideIndex(Integer slideIndex);

    @Query("""
        select f from Feedback f
        join f.slide s
        where s.presentation.id = :presentationId
    """)
    List<Feedback> findByPresentationId(Long presentationId);
}
