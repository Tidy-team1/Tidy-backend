package com.tidy.tidy.web;

import com.tidy.tidy.infrastructure.storage.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final S3StorageService s3StorageService;

    @GetMapping("/presigned")
    public PresignedUrlResponse getPresignedUrl(@RequestParam String key) {
        // 30분 유효한 URL
        String url = s3StorageService.generatePresignedUrl(key, 30);
        return new PresignedUrlResponse(url);
    }

    public record PresignedUrlResponse(String url) {}
}
