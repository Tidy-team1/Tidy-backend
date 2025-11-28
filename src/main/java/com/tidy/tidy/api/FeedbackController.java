package com.tidy.tidy.api;

import com.tidy.tidy.api.dto.FeedbackResponse;
import com.tidy.tidy.domain.feedback.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/presentations")
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * 1) 발표 전체 피드백 조회
     */
    @GetMapping("/{presentationId}/feedbacks")
    public List<FeedbackResponse> getByPresentation(@PathVariable Long presentationId) {
        return feedbackService.getByPresentation(presentationId);
    }

    /**
     * 2) 특정 슬라이드 피드백 조회
     */
    @GetMapping("/{presentationId}/slides/{slideId}/feedbacks")
    public List<FeedbackResponse> getBySlide(@PathVariable Long slideId) {
        return feedbackService.getBySlide(slideId);
    }

    /**
     * 3) 피드백 적용
     * FE에서는 { "feedbackId": 123 } 형태로 요청
     */
    @PatchMapping("/feedbacks/{feedbackId}/apply")
    public void applyFeedback(@PathVariable Long feedbackId) {
        feedbackService.applyFeedback(feedbackId);
    }

    /**
     * 4) 피드백 무시(선택)
     */
    @PatchMapping("/feedbacks/{feedbackId}/ignore")
    public void ignoreFeedback(@PathVariable Long feedbackId) {
        feedbackService.ignoreFeedback(feedbackId);
    }
}