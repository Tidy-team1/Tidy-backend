package com.tidy.tidy.web;

import com.tidy.tidy.domain.presentation.Presentation;
import com.tidy.tidy.domain.presentation.PresentationService;
import com.tidy.tidy.web.dto.PresentationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspaces/{workspaceId}/presentations")
public class PresentationController {

    private final PresentationService presentationService;

    @PostMapping
    public ResponseEntity<PresentationResponse> uploadPresentation(
            @PathVariable Long workspaceId,
            @RequestParam("file") MultipartFile file
    ) {
        Presentation presentation = presentationService.savePresentation(workspaceId, file);
        return ResponseEntity.ok(new PresentationResponse(presentation));
    }
}
