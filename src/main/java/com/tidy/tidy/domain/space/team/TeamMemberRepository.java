package com.tidy.tidy.domain.space.team;

import com.tidy.tidy.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByUser(User user);
    List<TeamMember> findByTeamSpace(TeamSpace ts);
}
