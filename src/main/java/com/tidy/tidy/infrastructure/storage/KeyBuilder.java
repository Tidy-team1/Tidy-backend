package com.tidy.tidy.infrastructure.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyBuilder {

    @Value("${storage.base-dir}")
    private String baseDir;  // ex) "spaces"

    public String presentationOriginal(Long spaceId, Long presentationId) {
        return String.format("%s/%d/presentations/%d/v0/ppt/presentation.pptx",
                baseDir, spaceId, presentationId);
    }

    public static String versionedPptKey(Long spaceId, Long presentationId, Integer version) {
        return String.format(
                "spaces/%d/presentations/%d/v%d/ppt/presentation.pptx",
                spaceId, presentationId, version
        );
    }

    public String slideThumbnail(Long spaceId, Long presentationId, int index) {
        return String.format("%s/%d/presentations/%d/thumbnails/%d.png",
                baseDir, spaceId, presentationId, index);
    }
}
