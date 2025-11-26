package com.tidy.tidy.api.task.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateReviewTaskRequest {

    private Long presentationId;

    // 필요하면 spaceId, userId, 옵션 등 추가
     private Long spaceId;
    // private Long userId;

    // 향후 슬라이드/요소 단위 확장 시:
    private Integer slideIndex;    // optional
    private Integer elementIndex;  // optional

    private List<String> options;  // ⭐ 추가됨
}
