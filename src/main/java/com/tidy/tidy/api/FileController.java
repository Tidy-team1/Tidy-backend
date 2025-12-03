package com.tidy.tidy.api;

import com.tidy.tidy.infrastructure.storage.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class FileController {

    private final S3StorageService s3StorageService;

    // --- 단일 Presigned URL ---
    @GetMapping("/presigned")
    public PresignedUrlResponse getPresignedUrl(@RequestParam String key) {
        String url = s3StorageService.generatePresignedUrl(key, 30);
        return new PresignedUrlResponse(url);
    }

    public record PresignedUrlResponse(String url) {}

    // --- 여러 개 Presigned URL ---
    @PostMapping("/presigned/batch")
    public BatchPresignedUrlResponse getBatchPresignedUrl(@RequestBody BatchPresignedUrlRequest request) {
        Map<String, String> result = new HashMap<>();

        for (String key : request.keys()) {
            String url = s3StorageService.generatePresignedUrl(key, 30);
            result.put(key, url);
        }

        return new BatchPresignedUrlResponse(result);
    }

    public record BatchPresignedUrlRequest(List<String> keys) {}
    public record BatchPresignedUrlResponse(Map<String, String> urls) {}
}
