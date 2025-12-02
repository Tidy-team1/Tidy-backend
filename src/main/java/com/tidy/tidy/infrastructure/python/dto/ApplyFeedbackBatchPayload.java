package com.tidy.tidy.infrastructure.python.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ApplyFeedbackBatchPayload {

    private Long spaceId;
    private Long presentationId;

    private Integer baseVersion;
    private Integer targetVersion;

    private List<ApplyFeedbackPayload> items; // 여러 피드백을 묶어서 전송
}
