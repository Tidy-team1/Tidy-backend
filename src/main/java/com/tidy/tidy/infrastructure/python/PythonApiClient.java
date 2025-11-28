package com.tidy.tidy.infrastructure.python;

import com.tidy.tidy.api.task.dto.CreateReviewTaskRequest;
import com.tidy.tidy.infrastructure.python.dto.ApplyFeedbackPayload;
import com.tidy.tidy.infrastructure.python.dto.PptThumbnailRequest;
import com.tidy.tidy.infrastructure.python.dto.PptThumbnailResponse;

import com.tidy.tidy.infrastructure.python.dto.ReviewAnalysisResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PythonApiClient {

    @Value("${python.base-url}")
    private String pythonBaseUrl;

    private final RestTemplate pythonRestTemplate;

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
    public ReviewAnalysisResult requestReviewAnalysis(Long spaceId, Long presentationId, List<String> options) {

        String url = pythonBaseUrl + "/analysis/review";

        Map<String, Object> body = new HashMap<>();
        body.put("spaceId", spaceId);
        body.put("presentationId", presentationId);
        body.put("options", options);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<ReviewAnalysisResult> response =
                pythonRestTemplate.exchange(url, HttpMethod.POST, entity, ReviewAnalysisResult.class);

        return response.getBody();
    }

    public void applyFeedback(ApplyFeedbackPayload payload) {
    }
}
