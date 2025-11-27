package com.tidy.tidy.application.task;

import com.tidy.tidy.api.parsing.dto.PptParseResponse;
import com.tidy.tidy.domain.element.PresentationElementRepository;
import com.tidy.tidy.domain.element.PresentationElementService;
import com.tidy.tidy.domain.task.TaskStatus;
import com.tidy.tidy.domain.task.TaskStatusRepository;
import com.tidy.tidy.infrastructure.python.PythonApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParsingTaskAsyncService {

    private final TaskStatusRepository taskStatusRepository;
    private final PythonApiClient pythonApiClient;
    private final PresentationElementService elementService;

    @Async("taskExecutor")
    public void processParsingTask(Long taskId, Long spaceId, Long presId) {
        try {
            updateStatus(taskId, "PROCESSING");

            // 1) Python 파싱 요청
            PptParseResponse response = pythonApiClient.parsePpt(spaceId, presId);

            // 2) DB 저장
            elementService.saveParsedElements(presId, response);

            updateStatus(taskId, "DONE");

        } catch (Exception e) {
            updateStatus(taskId, "ERROR");
        }
    }

    private void updateStatus(Long taskId, String status) {
        TaskStatus ts = taskStatusRepository.findById(taskId)
                .orElseThrow();
        ts.updateStatus(status);
        taskStatusRepository.save(ts);
    }
}
