package com.tidy.tidy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class VersionedSlideListResponse {
    private int version;
    private int slideCount;
    private List<VersionedSlideResponse> slides;
}
