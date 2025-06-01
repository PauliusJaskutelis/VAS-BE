package com.fashiontrunk.fashiontrunkapi.unit.Controllers;

import com.fashiontrunk.fashiontrunkapi.Controllers.ClassificationController;
import com.fashiontrunk.fashiontrunkapi.Models.ModelEntity;
import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Services.ClassificationService;
import com.fashiontrunk.fashiontrunkapi.Services.ModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ClassificationControllerTest {

    @Mock
    private ModelService modelService;

    @Mock
    private ClassificationService classificationService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ClassificationController classificationController;

    private UserEntity user;
    private MockMultipartFile validFile;
    private MockMultipartFile emptyFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new UserEntity();
        when(authentication.getPrincipal()).thenReturn(user);

        validFile = createMockPngFile();
        emptyFile = new MockMultipartFile("files", "", "image/png", new byte[0]);
    }

    private MockMultipartFile createMockPngFile() {
        return new MockMultipartFile(
            "files",
            "test.png",
            "image/png",
            new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG header
                0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52          // Some fake IHDR chunk
            }
        );
    }

    @Test
    void classifyDefault_success() throws Exception {
        when(classificationService.sendToClassifier(any(), anyInt(), anyDouble()))
                .thenReturn("{\"results\": [\"cat\"]}");

        ResponseEntity<?> response = classificationController.classifyDefault(new MockMultipartFile[]{validFile}, 5, 0.1, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("cat"));
    }

    @Test
    void classifyDefault_invalidImageBundle() {
        ResponseEntity<?> response = classificationController.classifyDefault(new MockMultipartFile[]{emptyFile}, 5, 0.1, authentication);

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
    }

    @Test
    void classifyWithModel_success() throws Exception {
        ModelEntity modelEntity = new ModelEntity();
        modelEntity.setInputHeight(28);
        modelEntity.setInputWidth(28);
        modelEntity.setColorMode("RGB");
        modelEntity.setStoragePath("/models/test");

        when(modelService.getModelById(any())).thenReturn(Optional.of(modelEntity));
        when(classificationService.classifyWithModel(any(), anyString(), any(), anyInt(), anyDouble(), anyInt(), anyInt(), anyBoolean(), anyString()))
                .thenReturn("{\"results\": [\"dog\"]}");

        ResponseEntity<?> response = classificationController.classifyWithModel(UUID.randomUUID(), new MockMultipartFile[]{validFile}, "Test Model", 5, 0.1, false, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("dog"));
    }

    @Test
    void classifyWithModel_modelNotFound() {
        when(modelService.getModelById(any())).thenReturn(Optional.empty());

        ResponseEntity<?> response = classificationController.classifyWithModel(UUID.randomUUID(), new MockMultipartFile[]{validFile}, "Test Model", 5, 0.1, false,  authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void classifyWithModel_invalidImageBundle() {
        ResponseEntity<?> response = classificationController.classifyWithModel(UUID.randomUUID(), new MockMultipartFile[]{emptyFile}, "Test Model", 5, 0.1, false, authentication);

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
    }
}
