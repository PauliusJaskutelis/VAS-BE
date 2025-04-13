package com.fashiontrunk.fashiontrunkapi.Controllers;

import com.fashiontrunk.fashiontrunkapi.Models.ModelEntity;
import com.fashiontrunk.fashiontrunkapi.Services.MetadataService;
import com.fashiontrunk.fashiontrunkapi.Services.ModelService;
import com.fashiontrunk.fashiontrunkapi.Util.ModelStorage;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/models")
public class ModelController {
    private final ModelService modelService;
    private final MetadataService metadataService;

    @Autowired
    public ModelController(ModelService modelService, MetadataService metadataService) {
        this.modelService = modelService;
        this.metadataService = metadataService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ModelEntity> registerModel(@RequestPart("file") MultipartFile file) throws IOException, InterruptedException {
        UUID modelId = UUID.randomUUID();

        Map<String, Object> modelMetadata = metadataService.extractMetadata(file);

        ModelEntity model = new ModelEntity();
        model.setId(modelId);
        model.setFilename((String) modelMetadata.get("filename"));
        model.setStoragePath(ModelStorage.storeModelFile(file, modelId));
        model.setInputHeight((int) modelMetadata.get("input_height"));
        model.setInputWidth((int) modelMetadata.get("input_width"));
        model.setColorMode((String) modelMetadata.get("color_mode"));
        model.setPreprocessing((String) modelMetadata.get("preprocessing"));
        model.setInputShape((String) modelMetadata.getOrDefault("input_shape", "noshape"));

        return ResponseEntity.ok(modelService.saveModel(model));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getModelById(@PathVariable UUID id) {
        return modelService.getModelById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
