package com.tidy.tidy.infrastructure.python.dto;

import lombok.Getter;

@Getter
public class IssueElement {
    private Integer shapeId;
    private Integer elementIndex;
    private Integer bboxLeft;
    private Integer bboxTop;
    private Integer bboxWidth;
    private Integer bboxHeight;
    private String text;
    private String elementType;
}