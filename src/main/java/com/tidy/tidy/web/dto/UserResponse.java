package com.tidy.tidy.web.dto;

import com.tidy.tidy.domain.space.personal.PersonalSpace;
import com.tidy.tidy.domain.space.personal.PersonalSpaceRepository;
import com.tidy.tidy.domain.user.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
        Long id,
        String name,
        String email,
        String profileImage,
        String provider,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long personalSpaceId
) {
    public static UserResponse from(User user, PersonalSpace ps) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .provider(user.getProvider().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .personalSpaceId(ps != null ? ps.getId() : null)
                .build();
    }
}
