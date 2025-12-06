package com.tidy.tidy.domain.presentation.version;

import com.tidy.tidy.domain.BaseTimeEntity;
import com.tidy.tidy.domain.common.converter.LongListToJsonConverter;
import com.tidy.tidy.domain.presentation.Presentation;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    private Integer version;    // 적용 후 버전

    @Column(nullable = false)
    private Integer baseVersion;

    private int slideCount;

    @Column(nullable = false)
    private String pptS3Key;

    @Column(nullable = false)
    private String slidePrefix; // ex) .../slides/

    @Column(nullable = true, length = 1000)
    @Convert(converter = LongListToJsonConverter.class)
    private List<Long> appliedFeedbackIds;

}
