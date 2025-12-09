package com.tidy.tidy.domain.slide;

import com.tidy.tidy.domain.presentation.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SlideRepository extends JpaRepository<Slide, Long> {

    void deleteByPresentation(Presentation presentation);

    List<Slide> findAllByPresentationOrderBySlideIndexAsc(Presentation presentation);

    Optional<Slide> findByPresentation_IdAndSlideIndex(Long presentationId, Integer slideIndex);

    List<Slide> findByPresentation(Presentation p);

    List<Slide> findByPresentation_Id(Long presentationId);

    @Modifying
    @Query("delete from Slide s where s.presentation.id = :presentationId")
    void deleteAllByPresentationId(Long presentationId);
}
