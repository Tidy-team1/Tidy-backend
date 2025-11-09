package com.tidy.tidy.web;

import com.tidy.tidy.config.oauth.CustomOAuth2User;
import com.tidy.tidy.domain.user.User;
import com.tidy.tidy.domain.user.UserRepository;
import com.tidy.tidy.domain.workspace.Workspace;
import com.tidy.tidy.domain.workspace.WorkspaceService;
import com.tidy.tidy.web.dto.WorkspaceDetailResponse;
import com.tidy.tidy.web.dto.WorkspaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createWorkspace(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @RequestBody Map<String, String> request
    ) {
        String name = request.get("name");
        User user = userRepository.findByEmail(principal.getUser().getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        Workspace workspace = workspaceService.createWorkspace(user, name);
        return ResponseEntity.ok(Map.of("id", workspace.getId(), "name", workspace.getName()));
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceResponse>> getUserWorkspaces(
            @AuthenticationPrincipal CustomOAuth2User principal
    ) {
        User user = userRepository.findByEmail(principal.getUser().getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        List<WorkspaceResponse> workspaces = workspaceService.getUserWorkspaces(user)
                .stream()
                .map(WorkspaceResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(workspaces);
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceDetailResponse> getWorkspace(@PathVariable Long workspaceId) {
        var workspace = workspaceService.getWorkspace(workspaceId);
        return ResponseEntity.ok(WorkspaceDetailResponse.fromEntity(workspace));
    }
}
