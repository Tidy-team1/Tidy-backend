package com.tidy.tidy.api.task.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tidy.tidy.domain.task.TaskStatus;
import com.tidy.tidy.domain.task.TaskType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskStatusResponse {

    private final Long id;
    private final TaskType taskType;
    private final String status;

    // result를 JSON으로 내려주도록 Object 타입으로 변경
    private final Object result;

    private final Long presentationId;
    private final Integer slideIndex;
    private final Integer elementIndex;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public TaskStatusResponse(TaskStatus entity) {
        this.id = entity.getId();
        this.taskType = entity.getTaskType();
        this.status = entity.getStatus();

        // JSON 문자열을 Object로 파싱
        this.result = parseResult(entity.getResult());

        this.presentationId = entity.getPresentationId();
        this.slideIndex = entity.getSlideIndex();
        this.elementIndex = entity.getElementIndex();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }

    private Object parseResult(String json) {
        if (json == null) return null;
        try {
            return new ObjectMapper().readValue(json, Object.class);
        } catch (Exception e) {
            // JSON이 깨졌거나 파싱 실패 → 문자열 그대로 반환
            return json;
        }
    }
}
