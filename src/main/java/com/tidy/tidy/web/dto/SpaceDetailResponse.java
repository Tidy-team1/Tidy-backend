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

    private List<TeamMemberDto> members;              // TeamSpace 전용
    private List<PresentationResponse> presentations; // ⭐ 추가됨

    public static SpaceDetailResponse fromPersonal(
            PersonalSpace ps,
            List<PresentationResponse> presentations
    ) {
        return new SpaceDetailResponse(
                ps.getId(),
                ps.getName(),
                ps.getType().name(),
                ps.getCreatedAt(),
                null,
                presentations                               // ⭐ personal도 프레젠테이션 있음
        );
    }

    public static SpaceDetailResponse fromTeam(
            TeamSpace ts,
            List<TeamMember> teamMembers,
            List<PresentationResponse> presentations
    ) {
        List<TeamMemberDto> memberDtos = teamMembers.stream()
                .map(TeamMemberDto::fromEntity)
                .toList();

        return new SpaceDetailResponse(
                ts.getId(),
                ts.getName(),
                ts.getType().name(),
                ts.getCreatedAt(),
                memberDtos,
                presentations                               // ⭐ team도 프레젠테이션 포함
        );
    }

    public static SpaceDetailResponse from(
            Space space,
            List<TeamMember> teamMembers,
            List<PresentationResponse> presentations
    ) {
        if (space instanceof PersonalSpace ps) {
            return fromPersonal(ps, presentations);
        }
        if (space instanceof TeamSpace ts) {
            return fromTeam(ts, teamMembers, presentations);
        }
        throw new IllegalArgumentException("Unknown space type: " + space.getClass());
    }
}
