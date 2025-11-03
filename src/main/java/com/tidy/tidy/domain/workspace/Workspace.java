package com.tidy.tidy.domain.workspace;

import com.tidy.tidy.domain.membership.UserWorkspace;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 연결 테이블(UserWorkspace) 기반 다대다 관계
    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserWorkspace> userWorkspaces = new ArrayList<>();

    @Builder
    public Workspace(String name) {
        this.name = name;
    }

    // ---- 도메인 메서드 ----
    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("워크스페이스 이름은 비워둘 수 없습니다.");
        }
        this.name = name;
    }
}
