package com.tidy.tidy.domain.feedback;

import com.tidy.tidy.api.dto.FeedbackStatusDto;
import com.tidy.tidy.api.dto.FeedbackStatusResponse;
import com.tidy.tidy.domain.presentation.Presentation;
import com.tidy.tidy.domain.presentation.PresentationRepository;
import com.tidy.tidy.domain.presentation.version.PresentationRevision;
import com.tidy.tidy.domain.presentation.version.PresentationRevisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FeedbackStatusService {

    private final PresentationRepository presentationRepository;
    private final PresentationRevisionRepository revisionRepository;
    private final FeedbackRepository feedbackRepository;

    /**
     * 현재 버전까지의 revision을 모두 읽어서
     * 적용된 feedback ID 누적으로 계산
     */
    private Set<Long> computeAppliedSet(Presentation p) {
        int current = p.getCurrentVersion();

        // 초기 버전(예: 0)이면 적용된 피드백 없음
        if (current == 0) {
            return Collections.emptySet();
        }

        Set<Long> applied = new LinkedHashSet<>();

        // 1) currentVersion에 해당하는 revision부터 시작
        PresentationRevision rev =
                revisionRepository.findByPresentationAndVersion(p, current)
                        .orElse(null);

        // 2) baseVersion 체인을 따라 거꾸로 올라가면서 누적
        while (rev != null) {
            List<Long> delta = rev.getAppliedFeedbackIds();
            if (delta != null) {
                applied.addAll(delta);   // 이번 apply에서 추가된 것들
            }

            Integer baseVersion = rev.getBaseVersion();
            // 더 이상 거슬러 올라갈 버전이 없으면 종료 (예: 0 버전)
            if (baseVersion == null || baseVersion == 0) {
                break;
            }

            rev = revisionRepository.findByPresentationAndVersion(p, baseVersion)
                    .orElse(null);
        }

        return applied;
    }

    @Transactional(readOnly = true)
    public FeedbackStatusResponse getFeedbackStatus(Long presentationId) {

        Presentation p = presentationRepository.findById(presentationId)
                .orElseThrow(() -> new IllegalArgumentException("Presentation not found"));

        // 누적 적용 집합 계산
        Set<Long> appliedSet = computeAppliedSet(p);

        // 전체 feedback 리스트
        List<Feedback> feedbacks = feedbackRepository.findByPresentationId(p.getId());

        // DTO 변환
        List<FeedbackStatusDto> list = feedbacks.stream()
                .map(f -> new FeedbackStatusDto(
                        f.getId(),
                        f.getSlide().getSlideIndex(),
                        appliedSet.contains(f.getId())
                ))
                .toList();

        return new FeedbackStatusResponse(
                p.getCurrentVersion(),
                list
        );
    }
}
