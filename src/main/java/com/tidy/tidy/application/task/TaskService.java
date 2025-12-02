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
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

        // 1) Presentation 조회
        Presentation p = presentationRepository.findById(presentationId)
                .orElseThrow(() -> new IllegalArgumentException("Presentation not found"));

        int baseVersion = p.getCurrentVersion();
        int targetVersion = p.getMaxVersion() + 1;

        // 2) payload 준비
        List<ApplyFeedbackPayload> items = feedbackIds.stream()
                .map(id -> feedbackRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Feedback not found")))
                .map(f -> ApplyFeedbackPayload.builder()
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

        // 3) 버전 증가
        p.updateToNewVersion(targetVersion);

        // 4) Revision 저장
        PresentationRevision revision = PresentationRevision.builder()
                .presentation(p)
                .version(targetVersion)
                .pptS3Key(String.format(
                        "spaces/%d/presentations/%d/v%d/ppt/presentation.pptx",
                        p.getSpace().getId(), p.getId(), targetVersion))
                .slidePrefix(String.format(
                        "spaces/%d/presentations/%d/v%d/slides/",
                        p.getSpace().getId(), p.getId(), targetVersion))
                .appliedFeedbackIds(new Gson().toJson(feedbackIds))
                .build();

        revisionRepository.save(revision);

        // 5) Task 생성
        TaskStatus task = TaskStatus.builder()
                .taskType(TaskType.MODIFY)
                .status("PENDING")
                .presentationId(p.getId())
                .slideId(null)
                .build();

        taskStatusRepository.save(task);

        // 6) 트랜잭션 끝난 다음 비동기 시작(단, afterCommit 필요 없음!)
    /*
      방법:
      - 단순히 return 후 @Async 메서드 내부에서 taskId로 다시 조회해도 됨.
      - 트랜잭션 내부에서는 Async 호출 하지 않는다.
    */

        Long taskId = task.getId();

        // 4) commit 이후에 async 실행
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
