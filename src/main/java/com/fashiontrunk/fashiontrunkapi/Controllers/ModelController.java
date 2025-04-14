package com.fashiontrunk.fashiontrunkapi.Controllers;

import com.fashiontrunk.fashiontrunkapi.Models.ModelEntity;
import com.fashiontrunk.fashiontrunkapi.Services.MetadataService;
import com.fashiontrunk.fashiontrunkapi.Services.ModelService;
import com.fashiontrunk.fashiontrunkapi.Util.ModelStatusMessage;
import com.fashiontrunk.fashiontrunkapi.Util.ModelStorage;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/models")
public class ModelController {
    private final ModelService modelService;
    private final MetadataService metadataService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    public ModelController(ModelService modelService, MetadataService metadataService) {
        this.modelService = modelService;
        this.metadataService = metadataService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ModelEntity> registerModel(
            @RequestPart("file") MultipartFile file,
            @RequestPart("modelId") String id,
            @RequestPart("name") String filename
    ) throws IOException, InterruptedException {

        UUID modelId = UUID.fromString(id);
        try {
            updateStatus(modelId, "EXTRACTING");
            Map<String, Object> modelMetadata = metadataService.extractMetadata(file);

            updateStatus(modelId, "VALIDATING");
            ModelEntity model = new ModelEntity();
            model.setId(modelId);
            model.setFilename(filename);
            model.setStoragePath(ModelStorage.storeModelFile(file, modelId));
            model.setInputHeight((int) modelMetadata.get("input_height"));
            model.setInputWidth((int) modelMetadata.get("input_width"));
            model.setColorMode((String) modelMetadata.get("color_mode"));
            model.setPreprocessing((String) modelMetadata.get("preprocessing"));
            model.setInputShape((String) modelMetadata.getOrDefault("input_shape", "noshape"));
            model.setStatus("READY");
            updateStatus(modelId, "READY");
            return ResponseEntity.ok(modelService.saveModel(model));
        } catch (Exception exception) {
            updateStatus(modelId, "ERROR");

            ModelEntity model = new ModelEntity();
            model.setId(modelId);
            model.setFilename(filename);
            model.setStatus("ERROR");

            modelService.saveModel(model);
            throw exception;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getModelById(@PathVariable UUID id) {
        return modelService.getModelById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ModelEntity>> getAllModels() {
        return ResponseEntity.ok(modelService.getAllModels());
    }
    public void updateStatus(UUID modelId, String status) {
        messagingTemplate.convertAndSend("/topic/model-status", new ModelStatusMessage(modelId, status));
    }
}
