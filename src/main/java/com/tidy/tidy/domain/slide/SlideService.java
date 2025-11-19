package com.tidy.tidy.domain.slide;

import com.tidy.tidy.domain.presentation.Presentation;
import com.tidy.tidy.domain.presentation.PresentationRepository;
import com.tidy.tidy.infrastructure.python.PythonApiClient;
import com.tidy.tidy.infrastructure.python.dto.PptThumbnailRequest;
import com.tidy.tidy.infrastructure.python.dto.PptThumbnailResponse;
import com.tidy.tidy.web.dto.SlideResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlideService {

    private final PresentationRepository presentationRepository;
    private final SlideRepository slideRepository;
    private final PythonApiClient pythonApiClient;

    @Value("${bucket.path}")
    private String bucketPath; // 호스트 기준: /output

    private static final String CONTAINER_BUCKET_PATH = "/app/output"; // 컨테이너 기준

    @Transactional
    public void generateThumbnails(Long presentationId) {

        // 1) Presentation 조회
        Presentation p = presentationRepository.findById(presentationId)
                .orElseThrow(() -> new IllegalArgumentException("Presentation not found"));

        String hostFilePath = p.getFilePath();
        if (hostFilePath == null || hostFilePath.isBlank()) {
            throw new IllegalStateException("Presentation filePath is empty");
        }

        // 2) host → container 경로변환
        String containerFilePath = hostFilePath.replace(bucketPath, CONTAINER_BUCKET_PATH);

        // 3) thumbnails 디렉토리 (컨테이너 기준)
        String containerBaseDir = containerFilePath.substring(0, containerFilePath.lastIndexOf("/"));
        String containerThumbnailDir = containerBaseDir + "/thumbnails";

        // 4) Python 요청
        PptThumbnailRequest req = new PptThumbnailRequest(
                containerFilePath,
                containerThumbnailDir
        );

        PptThumbnailResponse response =
                pythonApiClient.requestThumbnailGeneration(presentationId, req);

        if (response.getThumbnailPaths() == null || response.getThumbnailPaths().isEmpty()) {
            throw new IllegalStateException("Python returned no thumbnails");
        }

        // 5) 기존 슬라이드 삭제
        slideRepository.deleteByPresentation(p);

        // 6) 새 슬라이드 생성
        int index = 1;
        for (String containerThumbPath : response.getThumbnailPaths()) {

            // ⭐ 핵심: 컨테이너 경로 → 로컬 경로 변환
            String hostThumbPath = containerThumbPath.replace(CONTAINER_BUCKET_PATH, bucketPath);

            Slide sl = new Slide(index, hostThumbPath, p);
            slideRepository.save(sl);

            index++;
        }

        // ⭐ 대표 썸네일도 로컬 기준으로 저장
        String firstHostThumb = response.getThumbnailPaths().get(0)
                .replace(CONTAINER_BUCKET_PATH, bucketPath);

        p.updateSlideCount(index - 1);
        p.updateThumbnailUrl(firstHostThumb);
    }

    @Transactional(readOnly = true)
    public List<SlideResponse> getSlides(Long presentationId) {
        Presentation p = presentationRepository.findById(presentationId)
                .orElseThrow(() -> new IllegalArgumentException("Presentation not found"));

        List<Slide> slides = slideRepository
                .findAllByPresentationOrderBySlideIndexAsc(p);

        return slides.stream()
                .map(s -> new SlideResponse(
                        s.getId(),
                        s.getSlideIndex(),
                        s.getThumbnailUrl()
                ))
                .toList();
    }
}
