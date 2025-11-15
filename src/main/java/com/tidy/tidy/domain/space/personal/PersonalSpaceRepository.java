package com.tidy.tidy.domain.space.personal;

import com.tidy.tidy.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalSpaceRepository extends JpaRepository<PersonalSpace, Long> {
    Optional<PersonalSpace> findByOwner(User owner);
}

