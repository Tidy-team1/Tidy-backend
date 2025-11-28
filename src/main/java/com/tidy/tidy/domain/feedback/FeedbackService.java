package com.tidy.tidy.domain.feedback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tidy.tidy.api.dto.FeedbackResponse;
import com.tidy.tidy.domain.slide.SlideRepository;
import com.tidy.tidy.infrastructure.python.PythonApiClient;
import com.tidy.tidy.infrastructure.python.dto.ApplyFeedbackPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<FeedbackResponse> getBySlide(Long slideId) {
        return feedbackRepository.findBySlideId(slideId)
                .stream()
                .map(fb -> new FeedbackResponse(fb, om))
                .toList();
    }

    /**
     * 피드백 적용 처리 (Python에 수정 요청)
     */
    @Transactional
    public void applyFeedback(Long feedbackId) {

        Feedback fb = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found"));

        // 이미 적용된 피드백일 경우 중복 적용 방지
        if (fb.getStatus() == FeedbackStatus.APPLIED) {
            return;
        }

        var slide = fb.getSlide();

        // Python 요청용 payload 생성
        ApplyFeedbackPayload payload = ApplyFeedbackPayload.builder()
                .presentationId(slide.getPresentation().getId())
                .slideIndex(slide.getSlideIndex())

                .type(fb.getType())
                .detailsJson(fb.getDetails())

                .shapeId(fb.getShapeId())
                .elementIndex(fb.getElementIndex())

                .bboxLeft(fb.getBboxLeft())
                .bboxTop(fb.getBboxTop())
                .bboxWidth(fb.getBboxWidth())
                .bboxHeight(fb.getBboxHeight())
                .build();

        // Python API 호출
        pythonApiClient.applyFeedback(payload);

        // 상태 업데이트
        fb.updateStatus(FeedbackStatus.APPLIED);
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
}
