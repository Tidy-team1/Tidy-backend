package com.tidy.tidy.infrastructure.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyBuilder {

    @Value("${storage.base-dir}")
    private String baseDir;  // ex) "spaces"

    public String presentationOriginal(Long spaceId, Long presentationId) {
        return String.format("%s/%d/presentations/%d/original/presentation.pptx",
                baseDir, spaceId, presentationId);
    }

    public String slideThumbnail(Long spaceId, Long presentationId, int index) {
        return String.format("%s/%d/presentations/%d/thumbnails/slide-%d.png",
                baseDir, spaceId, presentationId, index);
    }

    public String aiResult(Long spaceId, Long presentationId) {
        return String.format("%s/%d/presentations/%d/ai/result.json",
                baseDir, spaceId, presentationId);
    }
}
