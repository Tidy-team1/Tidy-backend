package com.tidy.tidy.domain.task;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_status")
public class TaskStatus {

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
     * 결과 데이터 (JSON, 파일 키 등)
     */
    @Column(columnDefinition = "TEXT")
    private String result;

    /**
     * 어떤 PPT(Presentation)에 대한 작업인지
     */
    private Long presentationId;

    /**
     * 특정 슬라이드 작업일 경우 (Optional)
     */
    private Integer slideIndex;

    /**
     * 특정 요소 작업일 경우 (Optional)
     */
    private Integer elementIndex;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
