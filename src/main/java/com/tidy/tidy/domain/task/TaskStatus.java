package com.tidy.tidy.domain.task;

import com.tidy.tidy.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_status")
public class TaskStatus extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 작업 종류 (PPT 변환 / 리뷰 분석 / 슬라이드 분석 등)
     */
    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    /**
     * 작업 상태: PENDING / PROCESSING / DONE / ERROR
     */
    private String status;

    /**
     * 어떤 PPT(Presentation)에 대한 작업인지
     */
    private Long presentationId;

    /**
     * 특정 슬라이드 작업일 경우 (Optional)
     */
    private Long slideId;

    /**
     * MODIFY에서 버전 표시하기 위해 추가한 필드
     */
    private Integer newVersion;
}
