package com.tidy.tidy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PresentationScoreResponse {
    private Long presentationId;
    private List<SlideScoreResponse> scores;
}
