package com.tidy.tidy.api.task;

import com.tidy.tidy.api.task.dto.CreateReviewTaskRequest;
import com.tidy.tidy.api.task.dto.TaskCreateResponse;
import com.tidy.tidy.api.task.dto.TaskStatusResponse;
import com.tidy.tidy.application.task.TaskService;
import com.tidy.tidy.domain.task.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * 리뷰 분석 비동기 작업 생성 API
     */
    @PostMapping("/presentations/{presentationId}/review")
    public ResponseEntity<TaskCreateResponse> createReviewTask(
            @PathVariable Long presentationId,
            @RequestBody CreateReviewTaskRequest req
    ) {
        Long taskId = taskService.createReviewAnalysisTask(presentationId, req);
        return ResponseEntity.ok(new TaskCreateResponse(taskId));
    }

    /**
     * Task 상태 조회 API
     * 예: GET /tasks/{taskId}
     */
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskStatusResponse> getTaskStatus(
            @PathVariable Long id
    ) {
        TaskStatus task = taskService.getTask(id);
        return ResponseEntity.ok(new TaskStatusResponse(task));
    }
}
