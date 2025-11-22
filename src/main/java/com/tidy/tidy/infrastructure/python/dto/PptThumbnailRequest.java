package com.tidy.tidy.infrastructure.python.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PptThumbnailRequest {
    private Long spaceId;
    private Long presentationId;
}
