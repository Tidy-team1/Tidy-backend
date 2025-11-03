package com.tidy.tidy.domain.user;

import com.tidy.tidy.domain.BaseTimeEntity;
import com.tidy.tidy.domain.membership.UserWorkspace;
import com.tidy.tidy.domain.workspace.Workspace;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider; // 계정 종류 구분용, ex. 구글, 네이버, 서비스내

    // 속한 워크스페이스
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserWorkspace> userWorkspaces = new ArrayList<>();

    @Builder
    public User(String name, String email, String profileImage, Provider provider) {
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.provider = provider;
    }

    // ---- 도메인 메서드 ----
    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 비워둘 수 없습니다.");
        }
        this.name = name;
    }

    public void changeProfileImage(String imageUrl) {
        this.profileImage = imageUrl;
    }
}
