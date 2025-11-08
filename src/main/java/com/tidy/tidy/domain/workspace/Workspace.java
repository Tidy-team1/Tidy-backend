package com.tidy.tidy.domain.workspace;

import com.tidy.tidy.domain.BaseTimeEntity;
import com.tidy.tidy.domain.membership.UserWorkspace;
import com.tidy.tidy.domain.presentation.Presentation;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Workspace extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 🔹 유저-워크스페이스 관계
    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserWorkspace> userWorkspaces = new ArrayList<>();

    // 🔹 워크스페이스 내 프레젠테이션
    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Presentation> presentations = new ArrayList<>();

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

    // ✅ 연관관계 편의 메서드
    public void addPresentation(Presentation presentation) {
        presentations.add(presentation);
        presentation.setWorkspace(this); // Presentation 쪽 workspace 동기화
    }

    public void removePresentation(Presentation presentation) {
        presentations.remove(presentation);
        presentation.setWorkspace(null);
    }
}
