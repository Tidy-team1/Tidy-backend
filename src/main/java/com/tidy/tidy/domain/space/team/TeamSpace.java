package com.tidy.tidy.domain.space.team;

import com.tidy.tidy.domain.space.Space;
import com.tidy.tidy.domain.space.SpaceType;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamSpace extends Space {

    private String name;

    @Builder
    public TeamSpace(String name) {
        super(SpaceType.TEAM);
        this.name = name;
    }

    public static TeamSpace create(String name) {
        return TeamSpace.builder()
                .name(name)
                .build();
    }
}
