package com.tidy.tidy.domain.presentation;

import com.tidy.tidy.domain.space.Space;
import com.tidy.tidy.domain.space.personal.PersonalSpace;
import com.tidy.tidy.domain.space.personal.PersonalSpaceRepository;
import com.tidy.tidy.domain.space.team.TeamSpace;
import com.tidy.tidy.domain.space.team.TeamSpaceRepository;
import com.tidy.tidy.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PresentationService {

    private final PresentationRepository presentationRepository;
    private final PersonalSpaceRepository personalSpaceRepository;
    private final TeamSpaceRepository teamSpaceRepository;

    private static final String UPLOAD_DIR = "uploads";

    @Transactional
    public Presentation savePresentation(Long spaceId, MultipartFile file, User uploader) {

        // 1️⃣ spaceId에 해당하는 Space 찾기
        Space space = findSpaceById(spaceId);

        // 2️⃣ 업로드 디렉토리 생성
        String spaceDir = UPLOAD_DIR + File.separator + spaceId;
        File dir = new File(spaceDir);
        if (!dir.exists()) dir.mkdirs();

        // 3️⃣ 파일명 검증 및 확장자 추출
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("파일 이름이 비어 있습니다.");
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }

        // 4️⃣ UUID 기반 저장 파일명 생성
        String storedName = UUID.randomUUID().toString() + extension;

        // 5️⃣ 실제 저장 경로
        String filePath = spaceDir + File.separator + storedName;

        // 6️⃣ 파일 저장
        try {
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + e.getMessage());
        }

        // 7️⃣ Presentation 생성
        Presentation presentation = Presentation.builder()
                .title(originalFilename)
                .filePath(filePath)
                .thumbnailUrl(null) // 추후 썸네일 지원 시 채움
                .slideCount(0)      // 추후 분석 단계에서 셋팅
                .analysisStatus(AnalysisStatus.PENDING)
                .space(space)        // 핵심: Space 단방향 연결
                .uploader(uploader)  // uploader(User) 단방향 매핑이면 id 필요
                .build();

        // 8️⃣ 저장 후 반환
        return presentationRepository.save(presentation);
    }


    /**
     * Space 추상 타입을 통합 조회하는 로직
     * (SpaceRepository 없이 personal → team 순서로 조회)
     */
    private Space findSpaceById(Long id) {

        // PersonalSpace 먼저 조회
        PersonalSpace ps = personalSpaceRepository.findById(id).orElse(null);
        if (ps != null) return ps;

        // TeamSpace 조회
        TeamSpace ts = teamSpaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 스페이스가 존재하지 않습니다. id=" + id));

        return ts;
    }
}
