package com.tidy.tidy.domain.presentation;

import com.tidy.tidy.domain.BaseTimeEntity;
import com.tidy.tidy.domain.space.Space;
import com.tidy.tidy.domain.user.User;
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

    @Column(nullable = false)
    private Integer currentVersion;   // 현재 표시 버전

    @Column(nullable = false)
    private Integer maxVersion;       // 지금까지 생성된 최대 버전

    // 핵심 포인트: Space 하나만 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    // ✅ 빌더는 생성자 단에만 지정: id는 제외됨
    @Builder
    public Presentation(String title,
                        String filePath,
                        String thumbnailUrl,
                        Integer slideCount,
                        AnalysisStatus analysisStatus,
                        Integer currentVersion,
                        Integer maxVersion,
                        User uploader,
                        Space space) {
        this.title = title;
        this.filePath = filePath;
        this.thumbnailUrl = thumbnailUrl;
        this.slideCount = slideCount;
        this.analysisStatus = analysisStatus != null ? analysisStatus : AnalysisStatus.IMPORTED;
        this.currentVersion = currentVersion;
        this.maxVersion = maxVersion;
        this.uploader = uploader;
        this.space = space;
    }

    //== 상태 변경 메서드 ==//
    public void updateSlideCount(int count) {
        this.slideCount = count;
    }

    public void updateThumbnailUrl(String url) {
        this.thumbnailUrl = url;
    }

    public void updateFilePath(String savedPath) {
        this.filePath = savedPath;
    }

    // --- 편의 메서드 ---
    public void moveToVersion(int version) {
        this.currentVersion = version;
    }

    public int getNextVersion() {
        return this.maxVersion + 1;
    }

    public void updateToNewVersion(int newVersion) {
        this.maxVersion = newVersion;
        this.currentVersion = newVersion;
    }
}
