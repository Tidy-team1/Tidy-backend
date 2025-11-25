package com.tidy.tidy.api;

import com.tidy.tidy.domain.slide.SlideService;
import com.tidy.tidy.api.dto.SlideResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/presentations")
public class PresentationController {

    private final SlideService slideService;

    @GetMapping("/{presentationId}/slides")
    public ResponseEntity<List<SlideResponse>> getSlides(
            @PathVariable Long presentationId) {

        return ResponseEntity.ok(slideService.getSlides(presentationId));
    }

}
