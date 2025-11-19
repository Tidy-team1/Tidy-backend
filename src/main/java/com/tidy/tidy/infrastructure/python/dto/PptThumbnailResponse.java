package com.tidy.tidy.infrastructure.python.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PptThumbnailResponse {
    private List<String> thumbnailPaths;
}
