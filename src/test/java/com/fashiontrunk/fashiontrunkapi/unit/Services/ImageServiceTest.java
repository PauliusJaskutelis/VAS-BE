package com.fashiontrunk.fashiontrunkapi.unit.Services;

import com.fashiontrunk.fashiontrunkapi.Models.CatalogEntity;
import com.fashiontrunk.fashiontrunkapi.Models.ImageEntity;
import com.fashiontrunk.fashiontrunkapi.Repositories.CatalogRepository;
import com.fashiontrunk.fashiontrunkapi.Repositories.ImageRepository;
import com.fashiontrunk.fashiontrunkapi.Services.ImageService;
import com.fashiontrunk.fashiontrunkapi.Services.MetadataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageServiceTest {

    private ImageRepository imageRepository;
    private CatalogRepository catalogRepository;
    private MetadataService metadataService;
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        imageRepository = mock(ImageRepository.class);
        catalogRepository = mock(CatalogRepository.class);
        metadataService = mock(MetadataService.class);
        imageService = new ImageService(imageRepository, catalogRepository, metadataService);
    }

    @Test
    void getImagesFromCatalog_returnsImages() {
        UUID catalogId = UUID.randomUUID();
        CatalogEntity catalog = new CatalogEntity();
        catalog.setId(catalogId);

        ImageEntity image = new ImageEntity();
        when(imageRepository.findByCatalog(any(CatalogEntity.class))).thenReturn(List.of(image));

        List<ImageEntity> result = imageService.getImagesFromCatalog(catalogId);

        assertEquals(1, result.size());
        verify(imageRepository).findByCatalog(any(CatalogEntity.class));
    }

    @Test
    void getImageById_returnsImage_whenExists() {
        UUID imageId = UUID.randomUUID();
        ImageEntity image = new ImageEntity();
        image.setId(imageId);

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));

        ImageEntity result = imageService.getImageById(imageId);

        assertNotNull(result);
        assertEquals(imageId, result.getId());
        verify(imageRepository).findById(imageId);
    }

    @Test
    void getImageById_throwsException_whenNotFound() {
        UUID imageId = UUID.randomUUID();
        when(imageRepository.findById(imageId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imageService.getImageById(imageId);
        });

        assertEquals("Image not found", exception.getMessage());
    }

    @Test
    void deleteImage_deletes_whenExists() {
        UUID imageId = UUID.randomUUID();
        when(imageRepository.existsById(imageId)).thenReturn(true);

        assertDoesNotThrow(() -> imageService.deleteImage(imageId));
        verify(imageRepository).deleteById(imageId);
    }

    @Test
    void deleteImage_throwsException_whenNotFound() {
        UUID imageId = UUID.randomUUID();
        when(imageRepository.existsById(imageId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imageService.deleteImage(imageId);
        });

        assertEquals("Image not found", exception.getMessage());
    }
}
