package com.tidy.tidy.infrastructure.python.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

@Getter
public class IssueResult {
    private String type;
    private String message;
    private JsonNode details;
    private IssueElement element;
}
