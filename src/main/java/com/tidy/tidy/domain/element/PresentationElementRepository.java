package com.tidy.tidy.domain.element;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresentationElementRepository extends JpaRepository<PresentationElement, Long> {

    List<PresentationElement> findByPresentationId(Long presentationId);

    List<PresentationElement> findByPresentationIdAndSlideIndex(Long presId, Integer slideIndex);

    PresentationElement findByPresentationIdAndSlideIndexAndElementIndex(
            Long presId, Integer slideIndex, Integer elementIndex
    );

    void deleteByPresentationId(Long presentationId);
}
