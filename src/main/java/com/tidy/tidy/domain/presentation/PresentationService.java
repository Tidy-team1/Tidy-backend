package com.tidy.tidy.domain.presentation;

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

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PresentationService {

    private final PresentationRepository presentationRepository;
    private final PersonalSpaceRepository personalSpaceRepository;
    private final TeamSpaceRepository teamSpaceRepository;

    // ⭐ 새로 주입되는 두 개
    private final StorageService storageService;
    private final KeyBuilder keyBuilder;

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
                .slideCount(0)
                .analysisStatus(AnalysisStatus.PENDING)
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
}
