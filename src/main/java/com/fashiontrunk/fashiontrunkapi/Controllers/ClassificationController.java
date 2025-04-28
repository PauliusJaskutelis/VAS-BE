package com.fashiontrunk.fashiontrunkapi.Controllers;

import com.fashiontrunk.fashiontrunkapi.Models.ModelEntity;
import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Services.ClassificationService;
import com.fashiontrunk.fashiontrunkapi.Services.ModelService;
import com.fashiontrunk.fashiontrunkapi.Util.ImageValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/classify")
public class ClassificationController {

    private final ModelService modelService;
    private final ClassificationService classificationService;

    public ClassificationController(ModelService modelService, ClassificationService classificationService) {
        this.modelService = modelService;
        this.classificationService = classificationService;
    }

    @PostMapping
    public ResponseEntity<?> classifyDefault(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(name = "prediction_count", defaultValue = "5") int predictionCount,
            @RequestParam(name = "confidence_threshold", defaultValue = "0.1") double confidenceThreshold,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        List<Object> results = new ArrayList<>();

        if (!ImageValidation.isValidImageBundle(files)) {
            return new ResponseEntity<>("File Not Accepted", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        for (MultipartFile file : files) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> wrapped = new HashMap<>();
                String result = classificationService.sendToClassifier(file, predictionCount, confidenceThreshold);

                wrapped.put("filename", file.getOriginalFilename());
                wrapped.put("results", mapper.readValue(result, Map.class).get("results"));
                wrapped.put("user", user);
                results.add(wrapped);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error processing file: " + file.getOriginalFilename());
            }
        }

        return ResponseEntity.ok(results);
    }

    @PostMapping("/with-model/{id}")
    public ResponseEntity<?> classifyWithModel(
            @PathVariable UUID id,
            @RequestParam("files") MultipartFile[] images,
            @RequestParam("model_name") String modelName,
            @RequestParam("prediction_count") int predictionCount,
            @RequestParam("confidence_threshold") double confidenceThreshold,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        List<Object> results = new ArrayList<>();
        Optional<ModelEntity> model = modelService.getModelById(id);
        ModelEntity modelMetadata;

        if (!ImageValidation.isValidImageBundle(images)) {
            return new ResponseEntity<>("File Not Accepted", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        if (model.isEmpty()) {
            return new ResponseEntity<>("Model not Found", HttpStatus.NOT_FOUND);
        }

        modelMetadata = model.get();
        for (MultipartFile image : images) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> wrapped = new HashMap<>();
                String result = classificationService.classifyWithModel(
                        id,
                        modelMetadata.getStoragePath(),
                        image,
                        predictionCount,
                        confidenceThreshold,
                        modelMetadata.getInputHeight(),
                        modelMetadata.getInputWidth(),
                        false,
                        modelMetadata.getColorMode()
                );

                wrapped.put("filename", image.getOriginalFilename());
                wrapped.put("results", mapper.readValue(result, Map.class).get("results"));
                results.add(wrapped);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error processing file: " + image.getOriginalFilename());
            }
        }

        return ResponseEntity.ok(results);
    }
}
