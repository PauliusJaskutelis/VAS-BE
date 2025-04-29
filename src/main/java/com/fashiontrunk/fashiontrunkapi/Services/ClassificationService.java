package com.fashiontrunk.fashiontrunkapi.Services;

import com.fashiontrunk.fashiontrunkapi.Util.ModelStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@Service
public class ClassificationService extends ContainerServiceBase {

    private static final String CONTAINER_NAME = "custom-model-tf";
    private static final String IMAGE_NAME = "custom-model-tf";
    private static final int PORT = 5000;

    @Autowired
    private ModelStorage modelStorage;

    @Override
    protected String getContainerName(){ return CONTAINER_NAME; }

    @Override
    protected String getImageName(){ return IMAGE_NAME; }

    @Override
    protected int getPort(){ return PORT; }

    @Override
    protected String getHealthCheckUrl() { return "http://localhost:" + getPort() + "/docs"; }

    public String sendToClassifier(
            MultipartFile file,
            int predictionCount,
            double confidenceThreshold
    ) throws IOException, InterruptedException {

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

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl("http://localhost:" + PORT + "/classify")
                .queryParam("prediction_count", predictionCount)
                .queryParam("confidence_threshold", confidenceThreshold);
        String url = uriComponentsBuilder.toUriString();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(
                url, requestEntity, String.class
        );

        return response.getBody();
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

        File modelFile;
        MultiValueMap<String, Object> modelRequest = new LinkedMultiValueMap<>();
        MultiValueMap<String, Object> imageRequest = new LinkedMultiValueMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        System.out.println("Point minus one");
        startContainer();
        System.out.println("Point zero");
        boolean hasModel = false;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity("http://localhost:" + PORT + "/has-model", Map.class);
            hasModel = Boolean.TRUE.equals(response.getBody().get("loaded"));
        } catch (Exception e) {
            System.out.println(LocalDateTime.now() + "Failed to Check model status");
            throw new IOException("Failed to check model status", e);
        }
        System.out.println("Point half of one");
        // 📤 Only upload the model if needed
        if (!hasModel) {

            modelFile = modelStorage.getModelFile(modelId, storagePath);
            if(!modelFile.exists()){
                System.out.println("Model was not found" + modelFile.getAbsolutePath());
                throw new IOException("Model was not found" + modelFile.getAbsolutePath());
            }
            try {
                modelRequest.add("file", new FileSystemResource(modelFile));
                restTemplate.postForEntity("http://localhost:" + PORT + "/upload-model", new HttpEntity<>(modelRequest, headers), String.class);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        System.out.println("Point one");
        ByteArrayResource imageResource = new ByteArrayResource(image.getBytes()) {
            @Override public String getFilename() { return image.getOriginalFilename(); }
        };
        System.out.println("Point two");
        imageRequest.add("file", imageResource);

        String classifyUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:" + PORT + "/classify")
                .queryParam("prediction_count", predictionCount)
                .queryParam("confidence_threshold", confidenceThreshold)
                .queryParam("resize_height", resizeHeight)
                .queryParam("resize_width", resizeWidth)
                .queryParam("normalize", normalize)
                .queryParam("color_mode", colorMode)
                .toUriString();

        System.out.println("Paoint tree");
        ResponseEntity<String> response = restTemplate.postForEntity(classifyUrl, new HttpEntity<>(imageRequest, headers), String.class);
        System.out.println("Point four");
        return response.getBody();
    }
}
