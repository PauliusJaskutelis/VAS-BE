package com.fashiontrunk.fashiontrunkapi.Util;

import java.util.UUID;

public class ModelStatusMessage {
    private UUID modelId;
    private String status;

    public ModelStatusMessage(UUID modelId, String status) {
        this.modelId = modelId;
        this.status = status;
    }

    public UUID getModelId() {
        return modelId;
    }

    public void setModelId(UUID modelId) {
        this.modelId = modelId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}