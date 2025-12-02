package com.tidy.tidy.application.task;

import com.tidy.tidy.domain.task.TaskStatus;
import com.tidy.tidy.domain.task.TaskStatusRepository;
import com.tidy.tidy.infrastructure.python.PythonApiClient;
import com.tidy.tidy.infrastructure.python.dto.ApplyFeedbackBatchPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModifyTaskAsyncService {

    private final TaskStatusRepository taskStatusRepository;
    private final PythonApiClient pythonApiClient;

    @Async("modifyExecutor")
    public void processModifyTask(Long taskId, ApplyFeedbackBatchPayload payload) {

        TaskStatus task = taskStatusRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        try {
            task.setStatus("PROCESSING");
            taskStatusRepository.save(task);

            pythonApiClient.applyFeedbackBatch(payload);

            task.setStatus("DONE");
            taskStatusRepository.save(task);

        } catch (Exception e) {
            task.setStatus("ERROR");
            taskStatusRepository.save(task);
        }
    }
}
