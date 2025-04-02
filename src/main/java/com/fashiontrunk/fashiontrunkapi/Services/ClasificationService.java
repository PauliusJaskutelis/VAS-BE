package com.fashiontrunk.fashiontrunkapi.Services;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class ClasificationService {
    public static String sendToClassifier(MultipartFile file, int predictionCount, double confidenceThreshold) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl("http://localhost:5000/classify")
                .queryParam("prediction_count", predictionCount)
                .queryParam("confidence_threshold", confidenceThreshold);
        String url = uriComponentsBuilder.toUriString();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(
                url, requestEntity, String.class
        );

        return response.getBody();
    }
}
