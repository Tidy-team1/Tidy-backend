package com.tidy.tidy.application.task;

import com.nimbusds.jose.shaded.gson.Gson;
import com.tidy.tidy.domain.presentation.Presentation;
import com.tidy.tidy.domain.presentation.PresentationRepository;
import com.tidy.tidy.domain.presentation.version.PresentationRevision;
import com.tidy.tidy.domain.presentation.version.PresentationRevisionRepository;
import com.tidy.tidy.domain.task.TaskStatus;
import com.tidy.tidy.domain.task.TaskStatusRepository;
import com.tidy.tidy.infrastructure.python.PythonApiClient;
import com.tidy.tidy.infrastructure.python.dto.ApplyFeedbackBatchPayload;
import com.tidy.tidy.infrastructure.python.dto.ApplyFeedbackPayload;
import com.tidy.tidy.infrastructure.python.dto.ModifyResult;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModifyTaskAsyncService {

    private final TaskStatusRepository taskStatusRepository;
    private final PresentationRepository presentationRepository;
    private final PresentationRevisionRepository revisionRepository;
    private final PythonApiClient pythonApiClient;

    @Async("modifyExecutor")
    public void processModifyTask(Long taskId, ApplyFeedbackBatchPayload payload) {
        TaskStatus task = taskStatusRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        try {
            task.setStatus("PROCESSING");
            taskStatusRepository.save(task);

            // --- 1) 파이썬 실행 ---
            ModifyResult result = pythonApiClient.applyFeedbackBatch(payload);

            // --- 2) Revision 생성 ---
            Presentation p = presentationRepository.findById(payload.getPresentationId())
                    .orElseThrow();

            PresentationRevision revision = PresentationRevision.builder()
                    .presentation(p)
                    .version(payload.getTargetVersion())
                    .baseVersion(payload.getBaseVersion())
                    .slideCount(result.getSlideCount())               // ⭐ slideCount 저장
                    .pptS3Key(result.getPptS3Key())
                    .slidePrefix(result.getSlidePrefix())
                    .appliedFeedbackIds(new Gson().toJson(
                            payload.getItems().stream()
                                    .map(ApplyFeedbackPayload::getFeedbackId)  // ← Feedback 엔티티 ID 필요!
                                    .toList()
                    ))
                    .build();

            revisionRepository.save(revision);

            // --- 3) Presentation slideCount 업데이트 ---
            p.updateSlideCount(result.getSlideCount());
            presentationRepository.save(p);

            // --- 4) 태스크 완료 ---
            task.setStatus("DONE");
            taskStatusRepository.save(task);

        } catch (Exception e) {
            task.setStatus("ERROR");
            taskStatusRepository.save(task);
        }
    }
}
