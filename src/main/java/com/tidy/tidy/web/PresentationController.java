package com.tidy.tidy.web;

import com.tidy.tidy.config.oauth.CustomOAuth2User;
import com.tidy.tidy.domain.presentation.Presentation;
import com.tidy.tidy.domain.presentation.PresentationService;
import com.tidy.tidy.domain.user.User;
import com.tidy.tidy.web.dto.PresentationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spaces/{spaceId}/presentations")
public class PresentationController {

    private final PresentationService presentationService;

    @PostMapping
    public ResponseEntity<PresentationResponse> uploadPresentation(
            @PathVariable Long spaceId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        // 1) 인증 사용자 꺼내기
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        User uploader = principal.getUser();

        // 2) Presentation 저장
        Presentation presentation = presentationService.savePresentation(spaceId, file, uploader);

        // 3) 응답
        return ResponseEntity.ok(new PresentationResponse(presentation));
    }
}
