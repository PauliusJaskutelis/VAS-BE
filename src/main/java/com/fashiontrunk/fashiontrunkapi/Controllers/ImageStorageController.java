package com.fashiontrunk.fashiontrunkapi.Controllers;

import com.fashiontrunk.fashiontrunkapi.Dto.ImageDTO;
import com.fashiontrunk.fashiontrunkapi.Models.ImageEntity;
import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Services.ImageService;
import com.fashiontrunk.fashiontrunkapi.Util.ImageMapper;
import com.fashiontrunk.fashiontrunkapi.Util.ImageValidation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageStorageController {

    private final ImageService imageService;

    public ImageStorageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<ImageDTO>> uploadImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("catalogId") UUID catalogId,
            Authentication authentication
    ) throws IOException, InterruptedException {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        List<ImageEntity> savedImages = imageService.uploadImagesToCatalog(
                catalogId,
                user.getId(), // Paduodam userId
                files
        );

        List<ImageDTO> response = savedImages.stream()
                .map(ImageMapper::toDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/catalog/{catalogId}")
    public ResponseEntity<List<ImageEntity>> getImagesFromCatalog(@PathVariable UUID catalogId) {
        List<ImageEntity> images = imageService.getImagesFromCatalog(catalogId);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable UUID imageId) {
        ImageEntity image = imageService.getImageById(imageId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .body(image.getData());
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable UUID imageId) {
        try {
            imageService.deleteImage(imageId);
            return ResponseEntity.ok("Image deleted");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Image not found");
        }
    }
}
