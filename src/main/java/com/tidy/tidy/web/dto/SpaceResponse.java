package com.tidy.tidy.web.dto;

import com.tidy.tidy.domain.space.personal.PersonalSpace;
import com.tidy.tidy.domain.space.team.TeamSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SpaceResponse {

    private Long id;
    private String name;
    private String type;
    private LocalDateTime createdAt;

    public static SpaceResponse fromPersonal(PersonalSpace ps) {
        return new SpaceResponse(
                ps.getId(),
                ps.getName(),
                ps.getType().name(),
                ps.getCreatedAt()
        );
    }

    public static SpaceResponse fromTeam(TeamSpace ts) {
        return new SpaceResponse(
                ts.getId(),
                ts.getName(),
                ts.getType().name(),
                ts.getCreatedAt()
        );
    }
}

