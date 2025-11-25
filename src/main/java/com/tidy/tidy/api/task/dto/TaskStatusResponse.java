package com.tidy.tidy.api.task.dto;

import com.tidy.tidy.domain.task.TaskStatus;
import com.tidy.tidy.domain.task.TaskType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskStatusResponse {

    private final Long id;
    private final TaskType taskType;
    private final String status;
    private final String result;
    private final Long presentationId;
    private final Integer slideIndex;
    private final Integer elementIndex;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public TaskStatusResponse(TaskStatus entity) {
        this.id = entity.getId();
        this.taskType = entity.getTaskType();
        this.status = entity.getStatus();
        this.result = entity.getResult();
        this.presentationId = entity.getPresentationId();
        this.slideIndex = entity.getSlideIndex();
        this.elementIndex = entity.getElementIndex();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }
}
