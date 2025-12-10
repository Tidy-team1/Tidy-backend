package com.tidy.tidy.api.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    private String content;

    // 선택적으로 feedbackIds 포함 가능
    private List<Long> feedbackIds;
}
