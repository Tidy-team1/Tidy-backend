package com.tidy.tidy.domain.user;

import com.tidy.tidy.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")  // ✅ 예약어 피하는 용도
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

    @Column(nullable = false, unique = true)
    private String providerId;

    @Builder
    public User(String name, String email, String profileImage, Provider provider, String providerId) {
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.provider = provider;
        this.providerId = providerId;
    }

    public static User create(String email, String name, Provider provider, String profileImage) {
        return User.builder()
                .email(email)
                .name(name)
                .provider(provider)
                .profileImage(profileImage)
                .build();
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
