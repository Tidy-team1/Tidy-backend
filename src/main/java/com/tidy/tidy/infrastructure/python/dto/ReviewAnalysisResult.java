package com.tidy.tidy.infrastructure.python.dto;
import lombok.Getter;
import java.util.List;

@Getter
public class ReviewAnalysisResult {
    private List<SlideIssueResult> results;
}
