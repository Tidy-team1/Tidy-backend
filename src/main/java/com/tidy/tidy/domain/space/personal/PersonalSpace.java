package com.tidy.tidy.domain.space.personal;

import com.tidy.tidy.domain.space.Space;
import com.tidy.tidy.domain.space.SpaceType;
import com.tidy.tidy.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalSpace extends Space {
    /** 개인 스페이스 이름 */
    @Column(nullable = false)
    private String name;

    /** 개인 스페이스 주인 (User) */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Builder
    public PersonalSpace(String name, User owner) {
        super(SpaceType.PERSONAL);
        this.name = name;
        this.owner = owner;
    }

    /** 정적 생성 메서드 */
    public static PersonalSpace create(String name, User owner) {
        return PersonalSpace.builder()
                .name(name)
                .owner(owner)
                .build();
    }
}


