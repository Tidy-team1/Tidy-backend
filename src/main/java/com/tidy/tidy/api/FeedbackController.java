package com.tidy.tidy.api;

import com.tidy.tidy.api.dto.ApplyFeedbackRequest;
import com.tidy.tidy.api.dto.FeedbackResponse;
import com.tidy.tidy.application.task.TaskService;
import com.tidy.tidy.domain.feedback.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/presentations")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final TaskService taskService;

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
    @GetMapping("/{presentationId}/slides/{slideIndex}/feedbacks")
    public List<FeedbackResponse> getBySlide(@PathVariable Integer slideIndex) {
        return feedbackService.getBySlide(slideIndex);
    }

    /**
     * 4) 피드백 무시(선택)
     */
    @PatchMapping("/feedbacks/{feedbackId}/ignore")
    public void ignoreFeedback(@PathVariable Long feedbackId) {
        feedbackService.ignoreFeedback(feedbackId);
    }

    @PostMapping("/{presentationId}/apply")
    public ResponseEntity<?> apply(
            @PathVariable Long presentationId,
            @RequestBody ApplyFeedbackRequest req
    ) {
        Long taskId = taskService.createModifyTask(presentationId, req.getFeedbackIds());
        return ResponseEntity.ok(Map.of("taskId", taskId));
    }
}