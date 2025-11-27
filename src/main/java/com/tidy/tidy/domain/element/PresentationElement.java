package com.tidy.tidy.domain.element;

import com.tidy.tidy.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "presentation_element")
public class PresentationElement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Presentation FK (연관관계 없이 ID만 저장 — 성능/설계 이유로 권장)
    @Column(nullable = false)
    private Long presentationId;

    @Column(nullable = false)
    private Integer slideIndex;     // 슬라이드 번호

    @Column(nullable = false)
    private Integer elementIndex;   // 슬라이드 내 요소 번호

    @Column(nullable = false, length = 20)
    private String type;            // text, shape, image, table …

    // ----------- layout 정보 (bbox) -----------
    @Column(nullable = false)
    private Double leftPos;        // left (px)

    @Column(nullable = false)
    private Double topPos;         // top (px)

    @Column(nullable = false)
    private Double width;          // width (px)

    @Column(nullable = false)
    private Double height;         // height (px)

    @Column
    private Integer zIndex;         // 레이어 순서

    @Column(nullable = true)
    private Double rotation;        // 회전각(없으면 null)

    // ----------- python-pptx 구조 원본 JSON -----------
    @Column(columnDefinition = "JSON")
    private String detailJson;      // 텍스트/폰트/shape 정교한 정보 저장

    // ----------- 수정용 메서드들 -----------
    public void updateDetailJson(String json) {
        this.detailJson = json;
    }

    public void updateLayout(Double left, Double top, Double width, Double height) {
        this.leftPos = left;
        this.topPos = top;
        this.width = width;
        this.height = height;
    }

    public void updateZIndex(Integer zIndex) {
        this.zIndex = zIndex;
    }
}
