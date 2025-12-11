package com.tidy.tidy.domain.feedback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tidy.tidy.api.dto.FeedbackResponse;
import com.tidy.tidy.api.dto.PresentationScoreResponse;
import com.tidy.tidy.api.dto.SlideScoreResponse;
import com.tidy.tidy.domain.slide.SlideRepository;
import com.tidy.tidy.domain.task.TaskStatus;
import com.tidy.tidy.domain.task.TaskStatusRepository;
import com.tidy.tidy.domain.task.TaskType;
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
    private final TaskStatusRepository taskStatusRepository;

    /**
     * 발표 전체 피드백 조회
     */
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getByPresentation(Long presentationId) {
        TaskStatus latestReview = taskStatusRepository
                .findTopByPresentationIdAndTaskTypeOrderByIdDesc(presentationId, TaskType.REVIEW_ANALYSIS)
                .orElse(null);

        if (latestReview == null) return List.of();

        return feedbackRepository.findByTask(latestReview)
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

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getAllByPresentation(Long presentationId) {

        return feedbackRepository.findByPresentationId(presentationId)
                .stream()
                .map(fb -> new FeedbackResponse(fb, om))
                .toList();
    }
}
