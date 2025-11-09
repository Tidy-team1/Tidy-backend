package com.tidy.tidy.domain.presentation;

import com.tidy.tidy.domain.BaseTimeEntity;
import com.tidy.tidy.domain.user.User;
import com.tidy.tidy.domain.workspace.Workspace;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용 기본 생성자 (외부 new 방지)
public class Presentation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // DB 자동 생성 (빌더에 포함 X)

    @Column(nullable = false)
    private String title;  // 사용자 지정 제목 (또는 파일명)

    @Column(nullable = false)
    private String filePath;  // 서버/S3에 저장된 PPT 경로

    private String thumbnailUrl;  // 첫 슬라이드 썸네일 이미지 경로

    private Integer slideCount;   // 슬라이드 개수 (Python 분석 시 전달)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus analysisStatus; // PENDING, RUNNING, DONE, FAILED

    private String analysisPath;  // Python 결과 JSON 경로 (선택적)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id")
    private User uploader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    // ✅ 빌더는 생성자 단에만 지정: id는 제외됨
    @Builder
    public Presentation(String title,
                        String filePath,
                        String thumbnailUrl,
                        Integer slideCount,
                        AnalysisStatus analysisStatus,
                        User uploader,
                        Workspace workspace) {
        this.title = title;
        this.filePath = filePath;
        this.thumbnailUrl = thumbnailUrl;
        this.slideCount = slideCount;
        this.analysisStatus = analysisStatus != null ? analysisStatus : AnalysisStatus.PENDING;
        this.uploader = uploader;
        this.workspace = workspace;
    }

    //== 상태 변경 메서드 ==//
    public void updateAnalysisInfo(Integer slideCount, String thumbnailUrl, String analysisPath) {
        this.slideCount = slideCount;
        this.thumbnailUrl = thumbnailUrl;
        this.analysisPath = analysisPath;
        this.analysisStatus = AnalysisStatus.DONE;
    }

    public void markAnalysisFailed() {
        this.analysisStatus = AnalysisStatus.FAILED;
    }

    // ✅ 맘대로 호출하지 말것. 편의 메서드용임.
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

}
