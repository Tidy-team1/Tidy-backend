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

    private Integer slideIndex; // 1번 슬라이드인지 2번인지

    private String thumbnailUrl; // S3 썸네일 URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presentation_id")
    private Presentation presentation;

    public Slide(Integer slideIndex, String thumbnailUrl, Presentation presentation) {
        this.slideIndex = slideIndex;
        this.thumbnailUrl = thumbnailUrl;
        this.presentation = presentation;
    }
}
