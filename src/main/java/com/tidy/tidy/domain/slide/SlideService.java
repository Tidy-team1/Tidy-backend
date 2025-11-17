package com.tidy.tidy.domain.slide;

import com.tidy.tidy.domain.presentation.PresentationRepository;
import com.tidy.tidy.web.dto.SlideResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SlideService {

    private final SlideRepository slideRepository;

    public List<SlideResponse> getSlides(Long presentationId) {
        return slideRepository.findByPresentationIdOrderBySlideIndex(presentationId)
                .stream()
                .map(SlideResponse::new)
                .toList();
    }
}
