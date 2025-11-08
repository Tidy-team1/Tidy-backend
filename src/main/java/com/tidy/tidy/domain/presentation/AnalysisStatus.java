package com.tidy.tidy.domain.presentation;

public enum AnalysisStatus {
    PENDING,   // 업로드 완료, 아직 분석 요청 전
    RUNNING,   // Python 분석 중
    DONE,      // 분석 완료
    FAILED     // 분석 실패
}
