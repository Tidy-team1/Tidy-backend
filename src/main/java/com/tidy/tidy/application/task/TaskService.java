package com.tidy.tidy.application.task;

import com.nimbusds.jose.shaded.gson.Gson;
import com.tidy.tidy.api.task.dto.CreateReviewTaskRequest;
import com.tidy.tidy.api.task.dto.TaskStatusResponse;
import com.tidy.tidy.domain.feedback.FeedbackRepository;
import com.tidy.tidy.domain.presentation.Presentation;
import com.tidy.tidy.domain.presentation.PresentationRepository;
import com.tidy.tidy.domain.presentation.version.PresentationRevision;
import com.tidy.tidy.domain.presentation.version.PresentationRevisionRepository;
import com.tidy.tidy.domain.task.TaskStatus;
import com.tidy.tidy.domain.task.TaskStatusRepository;
import com.tidy.tidy.domain.task.TaskType;
import com.tidy.tidy.infrastructure.python.dto.ApplyFeedbackBatchPayload;
import com.tidy.tidy.infrastructure.python.dto.ApplyFeedbackPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskStatusRepository taskStatusRepository;
    private final ReviewTaskAsyncService reviewTaskAsyncService;
    private final PresentationRepository presentationRepository; // ⭐ 중요
    private final ModifyTaskAsyncService modifyTaskAsyncService;
    private final FeedbackRepository feedbackRepository;
    private final PresentationRevisionRepository revisionRepository;

    @Transactional
    public Long createReviewAnalysisTask(Long presentationId, CreateReviewTaskRequest req) {
        // 1) presId → spaceId 조회
        Presentation pres = presentationRepository.findById(presentationId)
                .orElseThrow(() -> new IllegalArgumentException("Presentation not found"));
        Long spaceId = pres.getSpace().getId();

        // 2) TaskStatus 생성
        TaskStatus task = TaskStatus.builder()
                .taskType(TaskType.REVIEW_ANALYSIS)
                .status("PENDING")
                .presentationId(presentationId)
                .slideId(null)
                .build();

        taskStatusRepository.save(task);

        // 3) Async 로직에 options 포함하여 전달
        reviewTaskAsyncService.processReviewTask(task.getId(), spaceId, presentationId, req.getOptions());

        return task.getId();
    }

    public TaskStatusResponse getTask(Long taskId) {
        TaskStatus task = taskStatusRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        return new TaskStatusResponse(task);
    }

    public List<TaskStatusResponse> getTasksByPresentation(Long presentationId) {
        List<TaskStatus> tasks = taskStatusRepository.findByPresentationId(presentationId);
        return tasks.stream()
                .map(TaskStatusResponse::new)
                .toList();
    }

    @Transactional
    public Long createModifyTask(Long presentationId, List<Long> feedbackIds) {

        Presentation p = presentationRepository.findById(presentationId)
                .orElseThrow(() -> new IllegalArgumentException("Presentation not found"));

        int baseVersion = p.getCurrentVersion();
        int targetVersion = p.getMaxVersion() + 1;

        // --- Payload 생성 ---
        List<ApplyFeedbackPayload> items = feedbackIds.stream()
                .map(id -> feedbackRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Feedback not found")))
                .map(f -> ApplyFeedbackPayload.builder()
                        .feedbackId(f.getId())                 // ⭐ 추가
                        .presentationId(p.getId())
                        .slideIndex(f.getSlideIndex())
                        .type(f.getType())
                        .detailsJson(f.getDetails())
                        .shapeId(f.getShapeId())
                        .elementIndex(f.getElementIndex())
                        .bboxLeft(f.getBboxLeft())
                        .bboxTop(f.getBboxTop())
                        .bboxWidth(f.getBboxWidth())
                        .bboxHeight(f.getBboxHeight())
                        .build()
                )
                .toList();

        ApplyFeedbackBatchPayload batchPayload = ApplyFeedbackBatchPayload.builder()
                .spaceId(p.getSpace().getId())
                .presentationId(p.getId())
                .baseVersion(baseVersion)
                .targetVersion(targetVersion)
                .items(items)
                .build();

        // --- 버전 증가 (⭐ 여기에서만 version 증가) ---
        p.updateToNewVersion(targetVersion);

        // --- TaskStatus 생성 ---
        TaskStatus task = TaskStatus.builder()
                .taskType(TaskType.MODIFY)
                .status("PENDING")
                .presentationId(p.getId())
                .newVersion(targetVersion)
                .build();

        taskStatusRepository.save(task);

        Long taskId = task.getId();

        // --- commit 후 async 실행 ---
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        modifyTaskAsyncService.processModifyTask(taskId, batchPayload);
                    }
                }
        );

        return taskId;
    }

}
