package com.tidy.tidy.domain.presentation;

import com.tidy.tidy.api.dto.UndoResponse;
import com.tidy.tidy.domain.slide.Slide;
import com.tidy.tidy.domain.slide.SlideRepository;
import com.tidy.tidy.domain.space.Space;
import com.tidy.tidy.domain.space.personal.PersonalSpace;
import com.tidy.tidy.domain.space.personal.PersonalSpaceRepository;
import com.tidy.tidy.domain.space.team.TeamSpaceRepository;
import com.tidy.tidy.domain.user.User;
import com.tidy.tidy.infrastructure.storage.KeyBuilder;
import com.tidy.tidy.infrastructure.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.tidy.tidy.domain.presentation.version.PresentationRevision;
import com.tidy.tidy.domain.presentation.version.PresentationRevisionRepository;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PresentationService {

    private final PresentationRepository presentationRepository;
    private final PersonalSpaceRepository personalSpaceRepository;
    private final TeamSpaceRepository teamSpaceRepository;
    private final PresentationRevisionRepository revisionRepository;

    // ⭐ 새로 주입되는 두 개
    private final StorageService storageService;
    private final KeyBuilder keyBuilder;
    private final SlideRepository slideRepository;

    @Transactional
    public Presentation savePresentation(Long spaceId, MultipartFile file, User uploader) throws IOException {

        Space space = findSpaceById(spaceId);

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("파일 이름이 비어 있습니다.");
        }

        // 1) Presentation 메타데이터 먼저 저장해서 PK 생성
        Presentation pres = Presentation.builder()
                .title(originalFilename)
                .filePath("PENDING")  // NOT NULL 회피용 임시값
                .thumbnailUrl(null)
                .currentVersion(0)
                .maxVersion(0)
                .slideCount(0)
                .analysisStatus(AnalysisStatus.IMPORTED)
                .space(space)
                .uploader(uploader)
                .build();

        pres = presentationRepository.save(pres); // 여기서 id 생성
        Long presentationId = pres.getId();

        // ⭐ 2) S3 key 생성
        String key = keyBuilder.presentationOriginal(spaceId, presentationId);

        // ⭐ 3) StorageService(S3)로 파일 업로드
        String storedKey = storageService.upload(key, file.getBytes());

        // ⭐ 4) DB 업데이트
        pres.updateFilePath(storedKey);

        return pres;
    }

    private Space findSpaceById(Long id) {
        PersonalSpace ps = personalSpaceRepository.findById(id).orElse(null);
        if (ps != null) return ps;

        return teamSpaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 스페이스가 존재하지 않습니다. id=" + id));
    }

    @Transactional
    public UndoResponse undo(Long presentationId) {

        Presentation p = presentationRepository.findById(presentationId)
                .orElseThrow(() -> new IllegalArgumentException("Presentation not found"));

        int current = p.getCurrentVersion();
        if (current == 0) {
            throw new IllegalStateException("Already at version 0");
        }

        PresentationRevision rev = revisionRepository
                .findByPresentationAndVersion(p, current)
                .orElseThrow(() -> new IllegalArgumentException("Revision not found"));

        int previous = rev.getBaseVersion();

        // ⭐ Undo: v0 → Slide 엔티티 사용
        if (previous == 0) {
            p.updateCurrentVersion(0);
            return buildV0UndoResponse(p);
        }

        // ⭐ 이전 버전으로 이동
        p.updateCurrentVersion(previous);

        List<String> slideKeys = buildSlideKeys(p, previous);

        return new UndoResponse(previous, slideKeys);
    }

    private List<String> buildSlideKeys(Presentation p, int version) {

        if (version == 0) {
            throw new IllegalStateException("Use buildV0UndoResponse() for version 0");
        }

        PresentationRevision rev = revisionRepository
                .findByPresentationAndVersion(p, version)
                .orElseThrow();

        int slideCount = rev.getSlideCount();
        Long spaceId = p.getSpace().getId();

        String prefix = String.format(
                "spaces/%d/presentations/%d/v%d/slides/",
                spaceId, p.getId(), version
        );

        return IntStream.range(0, slideCount)
                .mapToObj(i -> prefix + i + ".png")
                .toList();
    }

    private UndoResponse buildV0UndoResponse(Presentation p) {

        List<Slide> slides = slideRepository.findByPresentation(p);

        List<String> keys = slides.stream()
                .sorted(Comparator.comparingInt(Slide::getSlideIndex))
                .map(Slide::getThumbnailUrl) // 이미 S3 경로 보관됨
                .toList();

        return new UndoResponse(0, keys);
    }

}
