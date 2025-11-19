package com.tidy.tidy.infrastructure.python.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PptThumbnailRequest {
    private String pptPath;
    private String outputDir;
}
