package com.fashiontrunk.fashiontrunkapi.Util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class ModelStorage {

    private final String storageDir;

    public ModelStorage(@Value("${storage.path:${user.home}/ml-models}") String storageDir) {
        this.storageDir = storageDir;
    }

    public String storeModelFile(MultipartFile file, UUID modelId) throws IOException {
        Path dirPath = Paths.get(storageDir);
        System.out.println(dirPath);
        if(!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String filename = modelId + file.getOriginalFilename();
        Path destination = dirPath.resolve(filename);

        file.transferTo(destination.toFile());

        return destination.toAbsolutePath().toString();
    }
    public File getModelFile(UUID modelId, String storagePath) {
        Path path = Paths.get(storagePath);
        return path.toFile();
    }
}
