package com.tidy.tidy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VersionedSlideResponse {
    private int index;
    private double width;
    private double height;
    private String imageKey;   // 또는 imageUrl
}
