package com.tidy.tidy.web.dto;

import com.tidy.tidy.domain.presentation.Presentation;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PresentationResponse {
    private Long id;
    private String title;
    private String filePath;
    private String analysisStatus;
    private String thumbnailUrl; // ⭐추가

    public PresentationResponse(Presentation p) {
        this.id = p.getId();
        this.title = p.getTitle();
        this.filePath = p.getFilePath();
        this.analysisStatus = p.getAnalysisStatus().name();
        this.thumbnailUrl = p.getThumbnailUrl(); // Presentation 엔티티에 존재한다는 전제
    }
}
