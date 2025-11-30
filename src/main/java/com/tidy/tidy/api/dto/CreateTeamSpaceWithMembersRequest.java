package com.tidy.tidy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTeamSpaceWithMembersRequest {
    private String name;
    private List<MemberInviteRequest> members;
}
