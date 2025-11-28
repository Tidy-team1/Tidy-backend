package com.tidy.tidy.domain.feedback;

import com.tidy.tidy.domain.BaseTimeEntity;
import com.tidy.tidy.domain.slide.Slide;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "feedbacks")
public class Feedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------- Slide FK (필수) --------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slide_id", nullable = false)
    private Slide slide;

    // -------- Feedback info --------
    @Column(nullable = false, length = 50)
    private String type;   // spelling, font_consistency, readability 등

    @Column(nullable = false)
    private String message;

    // type별로 다른 세부 정보(JSON)
    @Column(columnDefinition = "json", nullable = false)
    private String details;

//    @Column(nullable = true)
//    private String aiSuggestion; 얘는 details별로 통일

    // -------- Element identifying data --------

    @Column(nullable = true)
    private Integer shapeId;       // PPT 내부 고유 ID

    @Column(nullable = true)
    private Integer elementIndex;  // 파싱 순서 기반 식별자

    @Column(nullable = true)
    private Integer bboxLeft;

    @Column(nullable = true)
    private Integer bboxTop;

    @Column(nullable = true)
    private Integer bboxWidth;

    @Column(nullable = true)
    private Integer bboxHeight;

    // -------- 상태(PENDING / APPLIED / IGNORED) --------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackStatus status;

    // 업데이트 편의 함수
    public void updateStatus(FeedbackStatus feedbackStatus) {
        this.status = feedbackStatus;
    }
}
