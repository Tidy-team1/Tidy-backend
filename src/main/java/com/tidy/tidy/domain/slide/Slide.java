package com.tidy.tidy.domain.slide;

import com.tidy.tidy.domain.BaseTimeEntity;
import com.tidy.tidy.domain.presentation.Presentation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Slide extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer slideIndex; // 0번 슬라이드, 1번 슬라이드 ...

    private String thumbnailUrl; // S3 썸네일 URL

    private double width;
    private double height;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presentation_id")
    private Presentation presentation;

    public Slide(Integer slideIndex, String thumbnailUrl, double width, double height, Presentation presentation) {
        this.slideIndex = slideIndex;
        this.thumbnailUrl = thumbnailUrl;
        this.width = width;
        this.height = height;
        this.presentation = presentation;
    }
}
