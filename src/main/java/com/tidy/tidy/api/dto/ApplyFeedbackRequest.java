package com.tidy.tidy.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApplyFeedbackRequest {
    private List<Long> feedbackIds;  // 사용자가 선택한 피드백 ID
}
