package com.tidy.tidy.application.task;

import com.tidy.tidy.api.task.dto.CreateReviewTaskRequest;
import com.tidy.tidy.domain.task.TaskStatus;
import com.tidy.tidy.domain.task.TaskStatusRepository;
import com.tidy.tidy.infrastructure.python.PythonApiClient;
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

    @Async("taskExecutor")
    @Transactional
    public void processReviewTask(Long taskId, Long spaceId, Long presentationId, List<String> options) {

        TaskStatus task = taskStatusRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        try {
            task.setStatus("PROCESSING");

            // ⭐ Python에 options 전달
            String result = pythonClient.requestReviewAnalysis(spaceId, presentationId, options);

            task.setStatus("DONE");
            task.setResult(result);

        } catch (Exception e) {
            task.setStatus("ERROR");
            task.setResult("ERROR: " + e.getMessage());
        }
    }

}
