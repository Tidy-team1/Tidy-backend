package com.tidy.tidy.domain.feedback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tidy.tidy.api.dto.FeedbackResponse;
import com.tidy.tidy.api.dto.PresentationScoreResponse;
import com.tidy.tidy.api.dto.SlideScoreResponse;
import com.tidy.tidy.domain.slide.SlideRepository;
import com.tidy.tidy.infrastructure.python.PythonApiClient;
import com.tidy.tidy.infrastructure.python.dto.ApplyFeedbackPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tidy.tidy.domain.slide.Slide;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final SlideRepository slideRepository;
    private final PythonApiClient pythonApiClient;
    private final ObjectMapper om;

    /**
     * 발표 전체 피드백 조회
     */
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getByPresentation(Long presentationId) {
        return feedbackRepository.findBySlidePresentationId(presentationId)
                .stream()
                .map(fb -> new FeedbackResponse(fb, om))
                .toList();
    }

    /**
     * 특정 슬라이드 피드백 조회
     */
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getBySlide(Integer slideIndex) {
        return feedbackRepository.findBySlideIndex(slideIndex)
                .stream()
                .map(fb -> new FeedbackResponse(fb, om))
                .toList();
    }

    /**
     * 피드백 무시 (상태만 변경)
     */
    @Transactional
    public void ignoreFeedback(Long feedbackId) {

        Feedback fb = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found"));

        fb.updateStatus(FeedbackStatus.IGNORED);
    }

    /**
     * 점수
     */
    @Transactional(readOnly = true)
    public PresentationScoreResponse getScoresByPresentation(Long presentationId) {

        List<Slide> slides = slideRepository.findByPresentation_Id(presentationId);

        List<SlideScoreResponse> result = slides.stream()
                .map(s -> new SlideScoreResponse(
                        s.getId(),
                        s.getSlideIndex(),
                        s.getReadabilityScore(),
                        s.getAestheticScore(),
                        s.getConsistencyScore()
                ))
                .sorted(Comparator.comparing(SlideScoreResponse::getSlideIndex))
                .toList();

        return new PresentationScoreResponse(presentationId, result);
    }

}
