package com.tidy.tidy.infrastructure.python;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tidy.tidy.api.parsing.dto.PptParseResponse;
import com.tidy.tidy.api.task.dto.CreateReviewTaskRequest;
import com.tidy.tidy.infrastructure.python.dto.PptThumbnailRequest;
import com.tidy.tidy.infrastructure.python.dto.PptThumbnailResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PythonApiClient {

    @Value("${python.base-url}")
    private String pythonBaseUrl;

    private final RestTemplate pythonRestTemplate;
    private final ObjectMapper objectMapper;

    public PptThumbnailResponse requestThumbnailGeneration(Long presentationId, PptThumbnailRequest req) {

        String url = pythonBaseUrl + "/presentations/" + presentationId + "/thumbnails";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PptThumbnailRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<PptThumbnailResponse> response = pythonRestTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                PptThumbnailResponse.class
        );

        return response.getBody();
    }

    /**
     * 리뷰 분석 요청 (예시)
     * 실제 요청/응답 스펙은 FastAPI에 맞게 수정하면 됨
     */
    public String requestReviewAnalysis(Long spaceId, Long presentationId, List<String> options) {

        String url = pythonBaseUrl + "/analysis/review";

        Map<String, Object> body = new HashMap<>();
        body.put("spaceId", spaceId);
        body.put("presentationId", presentationId);
        body.put("options", options);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                pythonRestTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }

    public PptParseResponse parsePpt(Long spaceId, Long presentationId) {

        String url = pythonBaseUrl + "/parsing/parse";
        ParseRequest req = new ParseRequest(spaceId, presentationId);

        // 1) 응답을 먼저 String으로 받기
        String rawJson = pythonRestTemplate.postForObject(url, req, String.class);

        // 2) RAW JSON 로그 출력
        log.info("[Python RAW Response] {}", rawJson);

        try {
            // 3) JSON을 DTO로 변환
            return objectMapper.readValue(rawJson, PptParseResponse.class);
        } catch (Exception e) {
            log.error("[ParsePpt] Failed to map response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse Python response JSON");
        }
    }

    @Getter
    @AllArgsConstructor
    private static class ParseRequest {
        private Long spaceId;
        private Long presentationId;
    }

}
