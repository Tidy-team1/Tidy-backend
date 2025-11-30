package com.tidy.tidy.infrastructure.python.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplyFeedbackPayload {

    private Long presentationId;
    private Integer slideIndex;

    // 피드백 종류 (예: "font_consistency", "spelling_grammar")
    private String type;

    // 타입별 수정 정보 JSON (Feedback.details 그대로)
    private String detailsJson;

    // 요소 식별 정보
    private Integer shapeId;
    private Integer elementIndex;

    // bbox (fallback & 디버깅용)
    private double bboxLeft;
    private double bboxTop;
    private double bboxWidth;
    private double bboxHeight;
}
