package com.tidy.tidy.api.task.dto;

import com.tidy.tidy.domain.task.TaskStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskStatusResponse {
    private final Long id;
    private final String taskType;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public TaskStatusResponse(TaskStatus entity) {
        this.id = entity.getId();
        this.taskType = entity.getTaskType().name();
        this.status = entity.getStatus();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }
}
