package com.tidy.tidy.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tidy.tidy.domain.feedback.Feedback;
import lombok.Getter;

@Getter
public class FeedbackResponse {

    private final Long id;
    private final Long slideId;
    private final Integer slideIndex;
    private final String type;
    private final String message;
    private final JsonNode details;
    private final String status;

    // bbox
    private final double bboxLeft;
    private final double bboxTop;
    private final double bboxWidth;
    private final double bboxHeight;

    private final Integer shapeId;
    private final Integer elementIndex;

    public FeedbackResponse(Feedback f, ObjectMapper om) {
        this.id = f.getId();
        this.slideId = f.getSlide().getId();
        this.slideIndex = f.getSlide().getSlideIndex();
        this.type = f.getType();
        this.message = f.getMessage();
        this.details = parseJson(om, f.getDetails());
        this.status = f.getStatus().name();

        this.bboxLeft = f.getBboxLeft();
        this.bboxTop = f.getBboxTop();
        this.bboxWidth = f.getBboxWidth();
        this.bboxHeight = f.getBboxHeight();

        this.shapeId = f.getShapeId();
        this.elementIndex = f.getElementIndex();
    }

    private JsonNode parseJson(ObjectMapper om, String json) {
        try {
            return om.readTree(json);
        } catch (Exception e) {
            return null;
        }
    }
}
