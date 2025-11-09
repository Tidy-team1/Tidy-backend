package com.tidy.tidy.web.dto;

import com.tidy.tidy.domain.presentation.Presentation;
import lombok.Getter;

@Getter
public class PresentationResponse {
    private Long id;
    private String title;
    private String filePath;
    private String analysisStatus;

    public PresentationResponse(Presentation p) {
        this.id = p.getId();
        this.title = p.getTitle();
        this.filePath = p.getFilePath();
        this.analysisStatus = p.getAnalysisStatus().name();
    }
}