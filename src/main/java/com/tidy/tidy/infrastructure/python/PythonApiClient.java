package com.tidy.tidy.infrastructure.python;

import com.tidy.tidy.infrastructure.python.dto.PptThumbnailRequest;
import com.tidy.tidy.infrastructure.python.dto.PptThumbnailResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
}
