package com.fashiontrunk.fashiontrunkapi.integration.Utils;

import com.fashiontrunk.fashiontrunkapi.Util.ModelStorage;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ModelStorageIT {

    @Test
    void testStoreAndGetModelFile() throws IOException {
        // Sukuriam fake MultipartFile
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "model.h5",
                "application/octet-stream",
                "fake-model-content".getBytes()
        );

        UUID modelId = UUID.randomUUID();
        String path = ModelStorage.storeModelFile(mockFile, modelId);

        File storedFile = ModelStorage.getModelFile(modelId, path);

        assertTrue(storedFile.exists());
        assertEquals(path, storedFile.getAbsolutePath());

        // Po testo, išvalom failą
        Files.deleteIfExists(storedFile.toPath());
    }
}