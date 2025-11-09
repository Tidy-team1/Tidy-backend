package com.tidy.tidy.domain.presentation;

import com.tidy.tidy.domain.workspace.Workspace;
import com.tidy.tidy.domain.workspace.WorkspaceRepository;
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
    private final WorkspaceRepository workspaceRepository;

    // 상대경로 (서버 실행 위치 기준)
    private static final String UPLOAD_DIR = "uploads";

    @Transactional
    public Presentation savePresentation(Long workspaceId, MultipartFile file) {
        // 1️⃣ 워크스페이스 검증
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 워크스페이스가 존재하지 않습니다."));

        // 2️⃣ 업로드 경로 생성
        String workspaceDir = UPLOAD_DIR + File.separator + workspaceId;
        File dir = new File(workspaceDir);
        if (!dir.exists()) dir.mkdirs();

        // 3️⃣ 원본 파일명 및 확장자 추출
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("파일 이름이 비어 있습니다.");
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex); // .pptx 포함
        }

        // 4️⃣ UUID 기반 저장 파일명 생성
        String uuid = UUID.randomUUID().toString();
        String storedFilename = uuid + extension; // 예: 6a7d9e4a-...-a98a.pptx

        // 5️⃣ 실제 저장 경로
        String filePath = workspaceDir + File.separator + storedFilename;

        // 6️⃣ 파일 저장
        try {
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());
            System.out.println("✅ 파일 저장 성공: " + path.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 저장 실패: " + e.getMessage());
        }

        // 7️⃣ Presentation 엔티티 생성
        Presentation presentation = Presentation.builder()
                .title(originalFilename)            // 사용자가 본래 업로드한 이름
                .filePath(filePath)                 // 서버 저장 경로 (UUID 파일명)
                .analysisStatus(AnalysisStatus.PENDING)
                .workspace(workspace)
                .build();

        // 8️⃣ 양방향 연관관계 동기화
        workspace.addPresentation(presentation);

        // 9️⃣ 저장 및 반환
        return presentationRepository.save(presentation);
    }
}
