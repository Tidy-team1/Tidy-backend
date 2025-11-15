package com.tidy.tidy.domain.space.team;

import com.tidy.tidy.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teamspace_id", nullable = false)
    private TeamSpace teamSpace;

    @Enumerated(EnumType.STRING)
    private TeamRole role;

    @Builder
    public TeamMember(User user, TeamSpace teamSpace, TeamRole role) {
        this.user = user;
        this.teamSpace = teamSpace;
        this.role = role;
    }

    public static TeamMember create(User user, TeamSpace teamSpace, TeamRole role) {
        return TeamMember.builder()
                .user(user)
                .teamSpace(teamSpace)
                .role(role)
                .build();
    }
}
