package com.tidy.tidy.api;

import com.tidy.tidy.api.dto.SlideListResponse;
import com.tidy.tidy.api.dto.VersionedSlideListResponse;
import com.tidy.tidy.domain.slide.SlideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/presentations")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PresentationController {

    private final SlideService slideService;

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


}
