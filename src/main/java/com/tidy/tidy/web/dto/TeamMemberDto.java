package com.tidy.tidy.web.dto;

import com.tidy.tidy.domain.space.team.TeamMember;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamMemberDto {

    private final Long userId;
    private final String userName;
    private final String role;

    public static TeamMemberDto fromEntity(TeamMember member) {
        return new TeamMemberDto(
                member.getUser().getId(),
                member.getUser().getName(),
                member.getRole().name()
        );
    }
}
