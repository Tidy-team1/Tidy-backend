package com.tidy.tidy.application.task;

import com.tidy.tidy.api.task.dto.CreateReviewTaskRequest;
import com.tidy.tidy.domain.presentation.Presentation;
import com.tidy.tidy.domain.presentation.PresentationRepository;
import com.tidy.tidy.domain.task.TaskStatus;
import com.tidy.tidy.domain.task.TaskStatusRepository;
import com.tidy.tidy.domain.task.TaskType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskStatusRepository taskStatusRepository;
    private final ReviewTaskAsyncService reviewTaskAsyncService;
    private final PresentationRepository presentationRepository; // ⭐ 중요

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

    @Transactional(readOnly = true)
    public TaskStatus getTask(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }
}
