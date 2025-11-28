package com.tidy.tidy.application.task;

import com.tidy.tidy.api.task.dto.CreateReviewTaskRequest;
import com.tidy.tidy.domain.feedback.Feedback;
import com.tidy.tidy.domain.feedback.FeedbackRepository;
import com.tidy.tidy.domain.feedback.FeedbackStatus;
import com.tidy.tidy.domain.slide.Slide;
import com.tidy.tidy.domain.slide.SlideRepository;
import com.tidy.tidy.domain.task.TaskStatus;
import com.tidy.tidy.domain.task.TaskStatusRepository;
import com.tidy.tidy.infrastructure.python.PythonApiClient;
import com.tidy.tidy.infrastructure.python.dto.IssueElement;
import com.tidy.tidy.infrastructure.python.dto.IssueResult;
import com.tidy.tidy.infrastructure.python.dto.ReviewAnalysisResult;
import com.tidy.tidy.infrastructure.python.dto.SlideIssueResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewTaskAsyncService {

    private final PythonApiClient pythonClient;
    private final TaskStatusRepository taskStatusRepository;
    private final FeedbackRepository feedbackRepository;
    private final SlideRepository slideRepository;

    @Async("taskExecutor")
    @Transactional
    public void processReviewTask(
            Long taskId,
            Long spaceId,
            Long presentationId,
            List<String> options
    ) {

        TaskStatus task = taskStatusRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        try {
            task.setStatus("PROCESSING");

            // ⭐ 1) Python에서 점검 결과 받기
            ReviewAnalysisResult result =
                    pythonClient.requestReviewAnalysis(spaceId, presentationId, options);

            // ⭐ 2) DB에 Feedback 저장
            saveFeedbacksFromResult(presentationId, result);

            // ⭐ 3) TaskStatus 결과 처리
            task.setStatus("DONE");

        } catch (Exception e) {
            log.error("Review processing error", e);
            task.setStatus("ERROR");
            task.setResult("ERROR: " + e.getMessage());
        }
    }

    private void saveFeedbacksFromResult(Long presentationId, ReviewAnalysisResult result) {

        for (SlideIssueResult slideRes : result.getResults()) {

            // Presentation + slideIndex로 slide 찾기
            Slide slide = slideRepository
                    .findByPresentation_IdAndSlideIndex(presentationId, slideRes.getSlide())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Slide not found for index " + slideRes.getSlide())
                    );

            for (IssueResult issue : slideRes.getIssues()) {

                IssueElement el = issue.getElement();

                Feedback fb = Feedback.builder()
                        .slide(slide)
                        .type(issue.getType())
                        .message(issue.getMessage())
                        .details(issue.getDetails().toString())
                        .shapeId(el.getShapeId())
                        .elementIndex(el.getElementIndex())
                        .bboxLeft(el.getBboxLeft())
                        .bboxTop(el.getBboxTop())
                        .bboxWidth(el.getBboxWidth())
                        .bboxHeight(el.getBboxHeight())
                        .status(FeedbackStatus.PENDING)
                        .build();

                feedbackRepository.save(fb);
            }
        }
    }
}
