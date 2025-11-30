package com.tidy.tidy.api.dto;

import com.tidy.tidy.domain.slide.Slide;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SlideResponse {

    private Long id;
    private Integer slideIndex;
    private double width;
    private double height;
    private String thumbnailUrl;

    public SlideResponse(Slide slide) {
        this.id = slide.getId();
        this.width = slide.getWidth();
        this.height = slide.getHeight();
        this.slideIndex = slide.getSlideIndex();
        this.thumbnailUrl = slide.getThumbnailUrl();
    }
}
