package com.tidy.tidy.infrastructure.python.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class SlideIssueResult {
    private int slide;
    private List<IssueResult> issues;

    /**
     * snake_case에서 camelCase는 자동 매핑됨
     */
    private Double readability_score;
    private Double aesthetic_score;
    private Double consistency_score;
}
