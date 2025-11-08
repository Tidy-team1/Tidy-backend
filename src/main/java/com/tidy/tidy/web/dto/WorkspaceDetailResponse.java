package com.tidy.tidy.web.dto;

import com.tidy.tidy.domain.membership.MemberRole;
import com.tidy.tidy.domain.membership.UserWorkspace;
import com.tidy.tidy.domain.workspace.Workspace;

import java.time.LocalDateTime;
import java.util.List;

public record WorkspaceDetailResponse(
        Long id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<MemberInfo> members
) {
    public static WorkspaceDetailResponse fromEntity(Workspace workspace) {
        List<MemberInfo> memberInfos = workspace.getUserWorkspaces()
                .stream()
                .map(MemberInfo::fromEntity)
                .toList();

        return new WorkspaceDetailResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getCreatedAt(),
                workspace.getUpdatedAt(),
                memberInfos
        );
    }

    public record MemberInfo(
            Long userId,
            String userName,
            MemberRole role
    ) {
        public static MemberInfo fromEntity(UserWorkspace uw) {
            return new MemberInfo(
                    uw.getUser().getId(),
                    uw.getUser().getName(),
                    uw.getRole()
            );
        }
    }
}
