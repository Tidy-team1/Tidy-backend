package com.tidy.tidy.api.dto.comment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CommentResponse {

    private Long id;
    private Long slideId;
    private Long userId;
    private String userName;
    private String userProfileImage;

    private String content;

    private List<Long> feedbackIds;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
