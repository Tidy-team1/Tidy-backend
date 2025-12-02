package com.tidy.tidy.domain.presentation.version;

import com.tidy.tidy.domain.BaseTimeEntity;
import com.tidy.tidy.domain.presentation.Presentation;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "presentation_revisions")
public class PresentationRevision extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Presentation presentation;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private String pptS3Key;

    @Column(nullable = false)
    private String slidePrefix; // ex) .../slides/

    @Column(nullable = true, length = 1000)
    private String appliedFeedbackIds; // JSON 배열 문자열
}
