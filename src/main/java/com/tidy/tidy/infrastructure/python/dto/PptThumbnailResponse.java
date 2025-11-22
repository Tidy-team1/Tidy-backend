package com.tidy.tidy.infrastructure.python.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PptThumbnailResponse {
    private List<String> thumbnailKeys;  // S3 key list
}
