package com.tidy.tidy.domain.slide;

import com.tidy.tidy.domain.presentation.Presentation;
import com.tidy.tidy.domain.presentation.PresentationRepository;
import com.tidy.tidy.infrastructure.python.PythonApiClient;
import com.tidy.tidy.infrastructure.python.dto.PptThumbnailRequest;
import com.tidy.tidy.infrastructure.python.dto.PptThumbnailResponse;
import com.tidy.tidy.web.dto.SlideResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SlideService {

    private final PresentationRepository presentationRepository;
    private final SlideRepository slideRepository;
    private final PythonApiClient pythonApiClient;

    @Transactional
    public void generateThumbnails(Long presentationId) {

        // 1) Presentation 조회
        Presentation p = presentationRepository.findById(presentationId)
                .orElseThrow(() -> new IllegalArgumentException("Presentation not found"));

        Long spaceId = p.getSpace().getId();

        // 2) Python에 보내는 요청: 더 이상 경로 X, ID만 보냄
        PptThumbnailRequest req = new PptThumbnailRequest(spaceId, presentationId);

        PptThumbnailResponse response =
                pythonApiClient.requestThumbnailGeneration(presentationId, req);

        List<String> keys = response.getThumbnailKeys();
        if (keys == null || keys.isEmpty()) {
            throw new IllegalStateException("Python returned no thumbnails");
        }

        // 3) 기존 슬라이드 삭제
        slideRepository.deleteByPresentation(p);

        // 4) 새 슬라이드 저장
        int index = 1;
        for (String key : keys) {
            Slide sl = new Slide(index, key, p); // thumbnailUrl ← 이제 S3 key
            slideRepository.save(sl);
            index++;
        }

        // 5) 대표 썸네일 저장
        p.updateSlideCount(keys.size());
        p.updateThumbnailUrl(keys.get(0)); // 첫 번째 썸네일 key
    }

    @Transactional(readOnly = true)
    public List<SlideResponse> getSlides(Long presentationId) {
        Presentation p = presentationRepository.findById(presentationId)
                .orElseThrow(() -> new IllegalArgumentException("Presentation not found"));

        return slideRepository.findAllByPresentationOrderBySlideIndexAsc(p)
                .stream()
                .map(s -> new SlideResponse(
                        s.getId(),
                        s.getSlideIndex(),
                        s.getThumbnailUrl()  // 이제 S3 key 그대로 반환
                )).toList();
    }
}
