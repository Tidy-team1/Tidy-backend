package com.tidy.tidy.domain.task;

public enum TaskType {

    // 리뷰 분석
    REVIEW_ANALYSIS,

    // PPT 파싱
    PARSE_PPT,

    // PPT 전체 -> PDF
    PPT_CONVERT,

    // PDF -> 이미지
    PDF_TO_IMAGE,

    // 전체 썸네일 생성
    THUMBNAIL_GENERATE,

    // 특정 슬라이드 재분석
    SLIDE_ANALYSIS,

    // 특정 요소 보정
    ELEMENT_CORRECTION
}
