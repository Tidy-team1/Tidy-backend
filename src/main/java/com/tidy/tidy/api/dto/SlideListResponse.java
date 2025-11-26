package com.tidy.tidy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SlideListResponse {
    private int slideCount;
    private List<SlideResponse> slides;
}
