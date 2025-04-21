package com.fashiontrunk.fashiontrunkapi.Services;

import com.fashiontrunk.fashiontrunkapi.Dto.ImageMetadataDTO;
import com.fashiontrunk.fashiontrunkapi.Models.CatalogEntity;
import com.fashiontrunk.fashiontrunkapi.Models.ImageEntity;
import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Repositories.CatalogRepository;
import com.fashiontrunk.fashiontrunkapi.Repositories.ImageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final CatalogRepository catalogRepository;
    private final MetadataService metadataService;

    public ImageService(ImageRepository imageRepository, CatalogRepository catalogRepository, MetadataService metadataService) {
        this.imageRepository = imageRepository;
        this.catalogRepository = catalogRepository;
        this.metadataService = metadataService;
    }

    @Transactional
    public List<ImageEntity> uploadImagesToCatalog(UUID catalogId, UUID userId, List<MultipartFile> files) throws IOException, InterruptedException {
        CatalogEntity catalog = catalogRepository.findById(catalogId)
                .orElseThrow(() -> new RuntimeException("Catalog not found"));

        List<ImageEntity> uploadedImages = new ArrayList<>();

        for (MultipartFile file : files) {
            ImageEntity image = new ImageEntity();
            image.setFilename(file.getOriginalFilename());
            image.setContentType(file.getContentType());
            image.setData(file.getBytes());
            image.setUploadDate(new Date());
            image.setCatalog(catalog);

            UserEntity user = new UserEntity(); user.setId(userId);
            image.setOwner(user);

            ImageEntity saved = imageRepository.save(image);

            try {
                ImageMetadataDTO metadata = metadataService.extractImageMetadata(file);
                saved.setWidth(metadata.getWidth());
                saved.setHeight(metadata.getHeight());
                saved.setColorMode(metadata.getColorMode());
                saved.setFormat(metadata.getFormat());

                imageRepository.save(saved); // Update with metadata
            } catch (Exception e) {
                System.out.println("⚠️ Failed to extract metadata for " + file.getOriginalFilename() + ": " + e.getMessage());
                // Optionally, palikti laukus null
            }

            uploadedImages.add(saved);
        }

        return uploadedImages;
    }

    public List<ImageEntity> getImagesFromCatalog(UUID catalogId) {
        CatalogEntity catalog = new CatalogEntity();
        catalog.setId(catalogId);
        return imageRepository.findByCatalog(catalog);
    }

    public ImageEntity getImageById(UUID imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }

    public void deleteImage(UUID imageId) {
        if (!imageRepository.existsById(imageId)) {
            throw new RuntimeException("Image not found");
        }
        imageRepository.deleteById(imageId);
    }
}

