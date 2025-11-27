package com.tidy.tidy.application.task;

import com.tidy.tidy.api.task.dto.CreateReviewTaskRequest;
import com.tidy.tidy.api.task.dto.TaskStatusResponse;
import com.tidy.tidy.domain.presentation.Presentation;
import com.tidy.tidy.domain.presentation.PresentationRepository;
import com.tidy.tidy.domain.task.TaskStatus;
import com.tidy.tidy.domain.task.TaskStatusRepository;
import com.tidy.tidy.domain.task.TaskType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskStatusRepository taskStatusRepository;
    private final ReviewTaskAsyncService reviewTaskAsyncService;
    private final PresentationRepository presentationRepository; // ⭐ 중요
    private final ParsingTaskAsyncService parsingTaskAsyncService;

    // ============================================
    // 1) 점검 Task 생성
    // ============================================
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
                .slideIndex(null)
                .elementIndex(null)
                .build();

        taskStatusRepository.save(task);

        // 3) Async 로직에 options 포함하여 전달
        reviewTaskAsyncService.processReviewTask(task.getId(), spaceId, presentationId, req.getOptions());

        return task.getId();
    }

    // ============================================
    // 2) 파싱 Task 생성
    // ============================================
    @Transactional
    public Long createParseTask(Long presentationId) {

        Presentation pres = presentationRepository.findById(presentationId)
                .orElseThrow(() -> new IllegalArgumentException("Presentation not found"));

        Long spaceId = pres.getSpace().getId();

        TaskStatus task = TaskStatus.builder()
                .taskType(TaskType.PARSE_PPT)
                .status("PENDING")
                .presentationId(presentationId)
                .build();

        taskStatusRepository.save(task);

        parsingTaskAsyncService.processParsingTask(
                task.getId(),
                spaceId,
                presentationId
        );

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
}
