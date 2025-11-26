package com.tidy.tidy.api;

import com.tidy.tidy.api.dto.SlideListResponse;
import com.tidy.tidy.domain.slide.SlideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/presentations")
public class PresentationController {

    private final SlideService slideService;

    @GetMapping("/{presentationId}/slides")
    public ResponseEntity<SlideListResponse> getSlides(
            @PathVariable Long presentationId) {

        return ResponseEntity.ok(slideService.getSlides(presentationId));
    }

}
