package com.tidy.tidy.domain.slide;

import com.tidy.tidy.api.dto.SlideListResponse;
import com.tidy.tidy.api.dto.VersionedSlideListResponse;
import com.tidy.tidy.api.dto.VersionedSlideResponse;
import com.tidy.tidy.domain.presentation.Presentation;
import com.tidy.tidy.domain.presentation.PresentationRepository;
import com.tidy.tidy.domain.presentation.version.PresentationRevision;
import com.tidy.tidy.domain.presentation.version.PresentationRevisionRepository;
import com.tidy.tidy.infrastructure.python.PythonApiClient;
import com.tidy.tidy.infrastructure.python.dto.PptThumbnailRequest;
import com.tidy.tidy.infrastructure.python.dto.PptThumbnailResponse;
import com.tidy.tidy.api.dto.SlideResponse;
import com.tidy.tidy.infrastructure.python.dto.SlideSizeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlideService {

    private final PresentationRepository presentationRepository;
    private final SlideRepository slideRepository;
    private final PythonApiClient pythonApiClient;
    private final PresentationRevisionRepository revisionRepository;

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
        List<SlideSizeDto> sizes = response.getSlides();

        if (keys == null || keys.isEmpty()) {
            throw new IllegalStateException("Python returned no thumbnails");
        }

        // 3) 기존 슬라이드 삭제
        slideRepository.deleteByPresentation(p);

        // 4) 새 슬라이드 저장
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            SlideSizeDto size = sizes.get(i);

            Slide sl = new Slide(
                    size.getIndex(),      // Python에서 넘겨준 index
                    key,
                    size.getWidth(),
                    size.getHeight(),
                    p
            );

            slideRepository.save(sl);
        }

        // 5) 대표 썸네일 저장
        p.updateSlideCount(keys.size());
        p.updateThumbnailUrl(keys.get(0)); // 첫 번째 썸네일 key
    }

    @Transactional(readOnly = true)
    public SlideListResponse getSlides(Long presentationId) {
        Presentation p = presentationRepository.findById(presentationId)
                .orElseThrow(() -> new IllegalArgumentException("Presentation not found"));

        List<SlideResponse> slides = slideRepository.findAllByPresentationOrderBySlideIndexAsc(p)
                .stream()
                .map(s -> new SlideResponse(
                        s.getId(),
                        s.getSlideIndex(),
                        s.getWidth(),
                        s.getHeight(),
                        s.getThumbnailUrl()
                ))
                .toList();

        return new SlideListResponse(slides.size(), slides);
    }

    public VersionedSlideListResponse getSlidesByVersion(Long presentationId, int version) {

        PresentationRevision rev = revisionRepository.findByPresentationIdAndVersion(presentationId, version)
                .orElseThrow(() -> new IllegalArgumentException("Revision not found"));

        int width = 1920;   // PPTX에서 width/height 읽어도 되고
        int height = 1080;

        List<VersionedSlideResponse> slides = new ArrayList<>();

        for (int i = 0; i < rev.getSlideCount(); i++) {
            slides.add(new VersionedSlideResponse(
                    i,
                    width,
                    height,
                    rev.getSlidePrefix() + i + ".png"
            ));
        }

        return new VersionedSlideListResponse(
                version,
                rev.getSlideCount(),
                slides
        );
    }

}
