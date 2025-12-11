package com.tidy.tidy.api;

import com.tidy.tidy.api.dto.*;
import com.tidy.tidy.application.task.TaskService;
import com.tidy.tidy.domain.feedback.FeedbackService;
import com.tidy.tidy.domain.feedback.FeedbackStatusService;
import com.tidy.tidy.domain.presentation.PresentationService;
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
    private final PresentationService presentationService;
    private final TaskService taskService;
    private final FeedbackStatusService feedbackStatusService;

    /**
     * 1) 발표 전체 피드백 조회
     */
    @GetMapping("/{presentationId}/feedbacks")
    public List<FeedbackResponse> getByPresentation(@PathVariable Long presentationId) {
        return feedbackService.getByPresentation(presentationId);
    }

    /**
     * 2) 특정 슬라이드 피드백 조회 -> 안 쓴대서 주석 처리
     */
//    @GetMapping("/{presentationId}/slides/{slideIndex}/feedbacks")
//    public List<FeedbackResponse> getBySlide(@PathVariable Integer slideIndex) {
//        return feedbackService.getBySlide(slideIndex);
//    }

    /**
     * 점수 조회 API
     */
    @GetMapping("/{presentationId}/scores")
    public PresentationScoreResponse getScores(@PathVariable Long presentationId) {
        return feedbackService.getScoresByPresentation(presentationId);
    }

    /**
     * 3) 피드백 적용
     */
    @PostMapping("/{presentationId}/apply")
    public ResponseEntity<?> apply(
            @PathVariable Long presentationId,
            @RequestBody ApplyFeedbackRequest req
    ) {
        Long taskId = taskService.createModifyTask(presentationId, req.getFeedbackIds());
        return ResponseEntity.ok(Map.of("taskId", taskId));
    }

    /**
     * 4) 되돌리기
     */
    @PostMapping("/{presentationId}/undo")
    public ResponseEntity<?> undo(
            @PathVariable Long presentationId
    ) {
        UndoResponse result = presentationService.undo(presentationId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{presentationId}/feedback-status")
    public ResponseEntity<FeedbackStatusResponse> getFeedbackStatus(
            @PathVariable Long presentationId
    ) {
        return ResponseEntity.ok(
                feedbackStatusService.getFeedbackStatus(presentationId)
        );
    }

    @GetMapping("/{presentationId}/feedbacks/all")
    public List<FeedbackResponse> getAllFeedbacks(@PathVariable Long presentationId) {
        return feedbackService.getAllByPresentation(presentationId);
    }

}