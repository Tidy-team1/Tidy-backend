package com.tidy.tidy.domain.workspace;

import com.tidy.tidy.domain.membership.MemberRole;
import com.tidy.tidy.domain.membership.UserWorkspace;
import com.tidy.tidy.domain.user.User;
import com.tidy.tidy.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    @Transactional
    public Workspace createWorkspace(User user, String name) {
        Workspace workspace = Workspace.builder()
                .name(name)
                .build();

        // UserWorkspace로 연결 (회원-워크스페이스 관계)
        UserWorkspace userWorkspace = UserWorkspace.builder()
                .user(user)
                .workspace(workspace)
                .role(MemberRole.OWNER)
                .build();

        workspace.getUserWorkspaces().add(userWorkspace);
        user.getUserWorkspaces().add(userWorkspace);

        return workspaceRepository.save(workspace);
    }

    public List<Workspace> getUserWorkspaces(User user) {
        return user.getUserWorkspaces()
                .stream()
                .map(UserWorkspace::getWorkspace)
                .toList();
    }

    public Workspace getWorkspace(Long workspaceId) {
        return workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 워크스페이스입니다."));
    }
}
