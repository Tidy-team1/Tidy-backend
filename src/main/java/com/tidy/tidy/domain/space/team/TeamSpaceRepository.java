package com.tidy.tidy.domain.space.team;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamSpaceRepository extends JpaRepository<TeamSpace, Long> {
    List<TeamSpace> findByNameContaining(String keyword);
}
