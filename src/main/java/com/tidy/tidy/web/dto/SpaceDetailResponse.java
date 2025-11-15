package com.tidy.tidy.web.dto;

import com.tidy.tidy.domain.space.Space;
import com.tidy.tidy.domain.space.personal.PersonalSpace;
import com.tidy.tidy.domain.space.team.TeamMember;
import com.tidy.tidy.domain.space.team.TeamSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class SpaceDetailResponse {

    private Long id;
    private String name;
    private String type;
    private LocalDateTime createdAt;

    private List<TeamMemberDto> members; // Personal이면 null

    public static SpaceDetailResponse fromPersonal(PersonalSpace ps) {
        return new SpaceDetailResponse(
                ps.getId(),
                ps.getName(),
                ps.getType().name(),
                ps.getCreatedAt(),
                null // personal space는 멤버 없음
        );
    }

    public static SpaceDetailResponse fromTeam(TeamSpace ts, List<TeamMember> teamMembers) {
        List<TeamMemberDto> members = teamMembers.stream()
                .map(TeamMemberDto::fromEntity)
                .toList();

        return new SpaceDetailResponse(
                ts.getId(),
                ts.getName(),
                ts.getType().name(),
                ts.getCreatedAt(),
                members
        );
    }

    public static SpaceDetailResponse from(Space space, List<TeamMember> teamMembers) {
        if (space instanceof PersonalSpace ps) {
            return fromPersonal(ps);
        }
        if (space instanceof TeamSpace ts) {
            return fromTeam(ts, teamMembers);
        }
        throw new IllegalArgumentException("Unknown space type: " + space.getClass());
    }
}
