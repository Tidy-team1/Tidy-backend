package com.tidy.tidy.infrastructure.python.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SlideSizeDto {
    private int index;
    private double width;
    private double height;
}