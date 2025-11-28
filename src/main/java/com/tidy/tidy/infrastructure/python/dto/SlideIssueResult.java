package com.tidy.tidy.infrastructure.python.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class SlideIssueResult {
    private int slide;
    private List<IssueResult> issues;
}
