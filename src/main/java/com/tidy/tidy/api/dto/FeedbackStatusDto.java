package com.tidy.tidy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedbackStatusDto {
    private Long feedbackId;
    private Integer slideIndex;
    private boolean applied;
}