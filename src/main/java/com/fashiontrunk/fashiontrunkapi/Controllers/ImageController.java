package com.fashiontrunk.fashiontrunkapi.Controllers;

import com.fashiontrunk.fashiontrunkapi.Services.ClasificationService;
import com.fashiontrunk.fashiontrunkapi.Util.ImageValidation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/image")
public class ImageController {

    @PostMapping
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "prediction_count", defaultValue = "5") int predictionCount,
            @RequestParam(name = "confidence_threshold", defaultValue = "0.1") double confidenceThreshold
    ) throws IOException {
        String contentType = file.getContentType();
        if (!Objects.equals(contentType, "image/png") &&
                !Objects.equals(contentType, "image/jpeg")) {
            return new ResponseEntity<>("Bad Header", HttpStatus.NOT_ACCEPTABLE);
        }
        if (!ImageValidation.isValidImage(file)) {
            return new ResponseEntity<>("File Not Accepted", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        String classificationResult = ClasificationService.sendToClassifier(file, predictionCount, confidenceThreshold);

        return ResponseEntity.ok(classificationResult);
    }
}
