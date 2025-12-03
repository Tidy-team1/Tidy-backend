package com.tidy.tidy.infrastructure.python.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModifyResult {
    private int slideCount;
    private String pptS3Key;
    private String slidePrefix;
}
