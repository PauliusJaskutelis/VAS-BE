package com.fashiontrunk.fashiontrunkapi.Controllers;

import com.fashiontrunk.fashiontrunkapi.Services.ClasificationService;
import com.fashiontrunk.fashiontrunkapi.Util.ImageValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/image")
public class ImageController {

    @PostMapping
    public ResponseEntity<?> uploadImage(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(name = "prediction_count", defaultValue = "5") int predictionCount,
            @RequestParam(name = "confidence_threshold", defaultValue = "0.1") double confidenceThreshold
    ) throws IOException {
        List<Object> results = new ArrayList<>();

        if (!ImageValidation.isValidImageBundle(files)) {
            return new ResponseEntity<>("File Not Accepted", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        for (MultipartFile file : files) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> wrapped = new HashMap<>();
                String result;

                result = ClasificationService.sendToClassifier(
                        file, predictionCount, confidenceThreshold
                );

                wrapped.put("filename", file.getOriginalFilename());
                wrapped.put("results", mapper.readValue(result, Map.class).get("results"));
                results.add(wrapped);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error processing file: " + file.getOriginalFilename());
            }
        }
        System.out.println(results);
        return ResponseEntity.ok(results);

    }
}
