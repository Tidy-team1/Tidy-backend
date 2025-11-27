package com.tidy.tidy.api.dto;

import com.tidy.tidy.domain.element.PresentationElement;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PresentationElementResponse {

    private final Long id;
    private final Long presentationId;
    private final Integer slideIndex;
    private final Integer elementIndex;
    private final String type;

    private final Double leftPos;
    private final Double topPos;
    private final Double width;
    private final Double height;
    private final Integer zIndex;
    private final Double rotation;

    private final String detailJson;

    public PresentationElementResponse(PresentationElement e) {
        this.id = e.getId();
        this.presentationId = e.getPresentationId();
        this.slideIndex = e.getSlideIndex();
        this.elementIndex = e.getElementIndex();
        this.type = e.getType();
        this.leftPos = e.getLeftPos();
        this.topPos = e.getTopPos();
        this.width = e.getWidth();
        this.height = e.getHeight();
        this.zIndex = e.getZIndex();
        this.rotation = e.getRotation();
        this.detailJson = e.getDetailJson();
    }
}
