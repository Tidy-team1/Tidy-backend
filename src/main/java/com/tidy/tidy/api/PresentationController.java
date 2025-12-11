package com.tidy.tidy.api;

import com.tidy.tidy.api.dto.PresentationMetadataResponse;
import com.tidy.tidy.api.dto.SlideListResponse;
import com.tidy.tidy.api.dto.VersionedSlideListResponse;
import com.tidy.tidy.domain.presentation.Presentation;
import com.tidy.tidy.domain.presentation.PresentationService;
import com.tidy.tidy.domain.slide.SlideService;
import com.tidy.tidy.infrastructure.storage.S3StorageService;
import com.tidy.tidy.infrastructure.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.tidy.tidy.infrastructure.storage.KeyBuilder.versionedPptKey;

@RestController
@RequiredArgsConstructor
@RequestMapping("/presentations")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PresentationController {

    private final SlideService slideService;
    private final PresentationService presentationService;
    private final StorageService storageService;

    @GetMapping("/{presentationId}/slides")
    public ResponseEntity<SlideListResponse> getSlides(
            @PathVariable Long presentationId) {

        return ResponseEntity.ok(slideService.getSlides(presentationId));
    }

    @GetMapping("/{presentationId}/versions/{version}/slides")
    public ResponseEntity<VersionedSlideListResponse> getSlidesByVersion(
            @PathVariable Long presentationId,
            @PathVariable Integer version
    ) {
        return ResponseEntity.ok(slideService.getSlidesByVersion(presentationId, version));
    }

    @GetMapping("/{presentationId}")
    public PresentationMetadataResponse getPresentationMetadata(
            @PathVariable Long presentationId) {
        return presentationService.getMetadata(presentationId);
    }

    @DeleteMapping("/{presentationId}")
    public void deletePresentation(@PathVariable Long presentationId) {
        presentationService.deletePresentation(presentationId);
    }

    @GetMapping("/{presentationId}/download")
    public ResponseEntity<byte[]> downloadPresentation(
            @PathVariable Long presentationId,
            @RequestParam(required = false) String filename) {

        Presentation pres = presentationService.getById(presentationId);

        // 실제 파일명 (한글 포함)
        String realFilename = (filename != null && !filename.isBlank())
                ? filename
                : pres.getTitle() + ".pptx";

        // UTF-8 인코딩된 파일명
        String encodedFilename = UriUtils.encode(realFilename, StandardCharsets.UTF_8);

        // Tomcat-safe fallback 파일명 (ASCII-only)
        String asciiFallback = "presentation.pptx";

        // 최종 Content-Disposition 헤더
        // filename="presentation.pptx"   → Tomcat-safe fallback
        // filename*=UTF-8''encodedName   → 실제 파일명, 모든 브라우저 지원됨
        String contentDisposition =
                "attachment; filename=\"" + asciiFallback + "\"; " +
                        "filename*=UTF-8''" + encodedFilename;

        // S3에서 파일 다운로드
        byte[] fileBytes = storageService.downloadFile(
                versionedPptKey(
                        pres.getSpace().getId(),
                        pres.getId(),
                        pres.getCurrentVersion()
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(fileBytes.length);

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

}
