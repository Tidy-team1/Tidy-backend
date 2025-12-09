package com.tidy.tidy.domain.slide;

import com.tidy.tidy.domain.presentation.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SlideRepository extends JpaRepository<Slide, Long> {

    void deleteByPresentation(Presentation presentation);

    List<Slide> findAllByPresentationOrderBySlideIndexAsc(Presentation presentation);

    Optional<Slide> findByPresentation_IdAndSlideIndex(Long presentationId, Integer slideIndex);

    List<Slide> findByPresentation(Presentation p);

    List<Slide> findByPresentation_Id(Long presentationId);
}
