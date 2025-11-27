package com.tidy.tidy.api.parsing.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ElementDto {
    private Integer elementIndex;
    private String type;
    private Double leftPos;
    private Double topPos;
    private Double width;
    private Double height;
    private Integer zIndex;
    private Double rotation;
    private Object detail;
}
