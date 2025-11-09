package com.tidy.tidy.web.dto;

import com.tidy.tidy.domain.workspace.Workspace;

import java.time.LocalDateTime;

public record WorkspaceResponse(
        Long id,
        String name,
        LocalDateTime createdAt
) {
    // 간단한 생성자 팩토리 메서드 (편의용)
    public static WorkspaceResponse fromEntity(Workspace workspace) {
        return new WorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getCreatedAt()
        );
    }
}
