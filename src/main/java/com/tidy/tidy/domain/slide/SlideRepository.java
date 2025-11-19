package com.tidy.tidy.domain.slide;

import com.tidy.tidy.domain.presentation.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlideRepository extends JpaRepository<Slide, Long> {

    void deleteByPresentation(Presentation presentation);

    List<Slide> findAllByPresentationOrderBySlideIndexAsc(Presentation presentation);
}
