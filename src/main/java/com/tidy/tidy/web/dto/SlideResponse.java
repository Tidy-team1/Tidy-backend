package com.tidy.tidy.web.dto;

import com.tidy.tidy.domain.slide.Slide;
import lombok.Getter;

@Getter
public class SlideResponse {

    private Long id;
    private Integer slideIndex;
    private String thumbnailUrl;

    public SlideResponse(Slide slide) {
        this.id = slide.getId();
        this.slideIndex = slide.getSlideIndex();
        this.thumbnailUrl = slide.getThumbnailUrl();
    }
}
