package com.tidy.tidy.api.task;

import com.tidy.tidy.api.task.dto.CreateReviewTaskRequest;
import com.tidy.tidy.api.task.dto.TaskCreateResponse;
import com.tidy.tidy.api.task.dto.TaskStatusResponse;
import com.tidy.tidy.application.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * PPT 요소 파싱 및 저장 비동기 작업 생성 API
     */
    @PostMapping("/presentations/{presentationId}/parse")
    public Long parsePresentation(@PathVariable Long presentationId) {
        return taskService.createParseTask(presentationId);
    }

    /**
     * Task 상태 조회 API
     * 예: GET /tasks/{taskId}
     */
    // 작업 단건 조회
    @GetMapping("/tasks/{taskId}")
    public TaskStatusResponse getTask(@PathVariable Long taskId) {
        return taskService.getTask(taskId);
    }

    // 특정 presentation에 연결된 작업 리스트 조회
    @GetMapping("/tasks")
    public List<TaskStatusResponse> getTasksByPresentation(@RequestParam Long presentationId) {
        return taskService.getTasksByPresentation(presentationId);
    }
}
