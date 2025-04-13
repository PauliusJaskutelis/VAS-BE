package com.fashiontrunk.fashiontrunkapi.Util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class ModelStorage {

    private static final String STORAGE_DIR = System.getProperty("user.home") + "/ml-models";

    public static String storeModelFile(MultipartFile file, UUID modelId) throws IOException {
        Path dirPath = Paths.get(STORAGE_DIR);
        System.out.println(dirPath);
        if(!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String filename = modelId +  file.getOriginalFilename();
        Path destination = dirPath.resolve(filename);

        file.transferTo(destination.toFile());

        return destination.toAbsolutePath().toString();
    }
}
