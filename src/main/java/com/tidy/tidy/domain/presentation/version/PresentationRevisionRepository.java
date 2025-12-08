package com.tidy.tidy.domain.presentation.version;

import com.tidy.tidy.domain.presentation.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PresentationRevisionRepository extends JpaRepository<PresentationRevision, Long> {
    Optional<PresentationRevision> findByPresentationAndVersion(Presentation presentation, Integer version);

    Optional<PresentationRevision> findByPresentationIdAndVersion(Long presentationId, int v);

    List<PresentationRevision> findByPresentationOrderByVersionAsc(Presentation presentation);
}
