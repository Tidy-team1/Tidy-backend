package com.tidy.tidy.domain.membership;

import com.tidy.tidy.domain.user.User;
import com.tidy.tidy.domain.workspace.Workspace;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserWorkspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 워크스페이스
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    // 역할
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Builder
    private UserWorkspace(User user, Workspace workspace, MemberRole role) {
        this.user = user;
        this.workspace = workspace;
        this.role = role;
    }

    // ---- 도메인 메서드 ----
    public void changeRole(MemberRole newRole) {
        if (newRole == null) {
            throw new IllegalArgumentException("역할은 null일 수 없습니다.");
        }
        this.role = newRole;
    }

    // 양방향 연관관계 편의 메서드
    public void setUser(User user) {
        this.user = user;
        if (!user.getUserWorkspaces().contains(this)) {
            user.getUserWorkspaces().add(this);
        }
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
        if (!workspace.getUserWorkspaces().contains(this)) {
            workspace.getUserWorkspaces().add(this);
        }
    }

}
