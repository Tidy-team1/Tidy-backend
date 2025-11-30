package com.tidy.tidy.infrastructure.python.dto;

import lombok.Getter;

@Getter
public class IssueElement {
    private Integer shapeId;
    private Integer elementIndex;
    private double bboxLeft;
    private double bboxTop;
    private double bboxWidth;
    private double bboxHeight;
    private String text;
    private String elementType;
}