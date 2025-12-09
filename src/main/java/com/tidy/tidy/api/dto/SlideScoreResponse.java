package com.tidy.tidy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SlideScoreResponse {
    private Long slideId;
    private Integer slideIndex;
    private Integer readabilityScore;
    private Integer aestheticScore;
    private Integer consistencyScore;
}
