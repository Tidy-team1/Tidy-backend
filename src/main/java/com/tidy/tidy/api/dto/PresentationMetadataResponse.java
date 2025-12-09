package com.tidy.tidy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PresentationMetadataResponse {
    private Long id;
    private Long spaceId;
    private String title;
    private Integer slideCount;
    private String fileSize; // bytes 단위
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
