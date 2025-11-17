package com.tidy.tidy.web;

import com.tidy.tidy.config.oauth.CustomOAuth2User;
import com.tidy.tidy.domain.presentation.Presentation;
import com.tidy.tidy.domain.presentation.PresentationService;
import com.tidy.tidy.domain.slide.SlideService;
import com.tidy.tidy.domain.user.User;
import com.tidy.tidy.web.dto.PresentationResponse;
import com.tidy.tidy.web.dto.SlideResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
