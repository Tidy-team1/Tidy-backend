package com.tidy.tidy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FeedbackStatusResponse {
    private Integer version;
    private List<FeedbackStatusDto> feedbacks;
}
