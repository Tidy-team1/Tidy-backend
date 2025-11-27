package com.tidy.tidy.api.parsing.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SlideDto {
    private Integer index;
    private List<ElementDto> elements;
}
