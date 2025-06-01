package com.fashiontrunk.fashiontrunkapi.Services;

import com.fashiontrunk.fashiontrunkapi.Util.ModelStorage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ClassificationService extends ContainerServiceBase {

    private static final String IMAGE_NAME = "custom-model-tfv2";
    private static final int BASE_PORT = 5000;

    private final Map<UUID, Integer> modelPortMap = new ConcurrentHashMap<>();

    private int getOrAssignPort(UUID modelId) {
        return modelPortMap.computeIfAbsent(modelId, id -> BASE_PORT + modelPortMap.size());
    }

    private String getContainerName(UUID modelId) {
        return "model-" + modelId.toString().substring(0, 8);
    }

    private String getBaseUrl(UUID modelId) {
        int port = getOrAssignPort(modelId);
        return "http://localhost:" + port;
    }

    private void ensureContainerRunning(UUID modelId) throws IOException, InterruptedException {
        String containerName = getContainerName(modelId);
        int port = getOrAssignPort(modelId);
        String healthUrl = getBaseUrl(modelId) + "/docs";

        startContainer(containerName, port, IMAGE_NAME, healthUrl);
    }

    public String classifyWithModel(
            UUID modelId,
            String storagePath,
            MultipartFile image,
            int predictionCount,
            double confidenceThreshold,
            int resizeHeight,
            int resizeWidth,
            boolean normalize,
            String colorMode
    ) throws IOException, InterruptedException {

        ensureContainerRunning(modelId);
        String baseUrl = getBaseUrl(modelId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Check if model is loaded
        boolean hasModel;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(baseUrl + "/has-model", Map.class);
            hasModel = Boolean.TRUE.equals(response.getBody().get("loaded"));
        } catch (Exception e) {
            throw new IOException("Failed to check model status", e);
        }

        if (!hasModel) {
            File modelFile = ModelStorage.getModelFile(modelId, storagePath);
            if (!modelFile.exists()) {
                throw new IOException("Model file not found: " + modelFile.getAbsolutePath());
            }

            MultiValueMap<String, Object> modelUpload = new LinkedMultiValueMap<>();
            modelUpload.add("file", new FileSystemResource(modelFile));
            restTemplate.postForEntity(baseUrl + "/upload-model", new HttpEntity<>(modelUpload, headers), String.class);
        }

        // Prepare image
        ByteArrayResource imageResource = new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> imageRequest = new LinkedMultiValueMap<>();
        imageRequest.add("file", imageResource);

        String classifyUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/classify")
                .queryParam("prediction_count", predictionCount)
                .queryParam("confidence_threshold", confidenceThreshold)
                .queryParam("resize_height", resizeHeight)
                .queryParam("resize_width", resizeWidth)
                .queryParam("normalize", normalize)
                .queryParam("color_mode", colorMode)
                .toUriString();

        ResponseEntity<String> response = restTemplate.postForEntity(
                classifyUrl,
                new HttpEntity<>(imageRequest, headers),
                String.class
        );

        return response.getBody();
    }

    // Legacy endpoint using static container (optional)
    public String sendToClassifier(
            MultipartFile file,
            int predictionCount,
            double confidenceThreshold
    ) throws IOException, InterruptedException {

        // Optional: fallback static container if needed
        startContainer();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        String url = UriComponentsBuilder
                .fromHttpUrl("http://localhost:" + getPort() + "/classify")
                .queryParam("prediction_count", predictionCount)
                .queryParam("confidence_threshold", confidenceThreshold)
                .toUriString();

        return restTemplate.postForEntity(
                url,
                new HttpEntity<>(body, headers),
                String.class
        ).getBody();
    }

    // Fallback static values for legacy base usage
    @Override
    protected String getContainerName() {
        return "default-container";
    }

    @Override
    protected String getImageName() {
        return IMAGE_NAME;
    }

    @Override
    protected int getPort() {
        return BASE_PORT;
    }

    @Override
    protected String getHealthCheckUrl() {
        return "http://localhost:" + getPort() + "/docs";
    }
}
