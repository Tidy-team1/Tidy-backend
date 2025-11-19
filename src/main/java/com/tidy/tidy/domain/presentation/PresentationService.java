package com.tidy.tidy.domain.presentation;

import com.tidy.tidy.domain.space.Space;
import com.tidy.tidy.domain.space.personal.PersonalSpace;
import com.tidy.tidy.domain.space.personal.PersonalSpaceRepository;
import com.tidy.tidy.domain.space.team.TeamSpaceRepository;
import com.tidy.tidy.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class PresentationService {

    private final PresentationRepository presentationRepository;
    private final PersonalSpaceRepository personalSpaceRepository;
    private final TeamSpaceRepository teamSpaceRepository;

    @Value("${bucket.path}")
    private String bucketPath; // 예: /output

    @Transactional
    public Presentation savePresentation(Long spaceId, MultipartFile file, User uploader) {

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

        // 2) 파일 경로 생성 (호스트 기준)
        //    /output/spaces/{spaceId}/presentations/{presentationId}/original.pptx
        String baseDir = bucketPath + "/spaces/" + spaceId + "/presentations/" + presentationId;
        File dir = new File(baseDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("디렉토리 생성 실패: " + baseDir);
        }

        String originalPath = baseDir + "/original.pptx";

        // 3) 파일 저장
        try {
            Files.write(Path.of(originalPath), file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + originalPath);
        }

        // 4) filePath 실제 값으로 업데이트
        pres.updateFilePath(originalPath);
        // 같은 @Transactional 안이라 flush 시점에 DB 반영됨

        // ❌ 여기서 썸네일 만들지 않는다
        return pres;
    }

    private Space findSpaceById(Long id) {
        PersonalSpace ps = personalSpaceRepository.findById(id).orElse(null);
        if (ps != null) return ps;

        return teamSpaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 스페이스가 존재하지 않습니다. id=" + id));
    }
}
