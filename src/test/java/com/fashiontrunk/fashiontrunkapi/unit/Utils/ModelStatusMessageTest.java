package com.fashiontrunk.fashiontrunkapi.unit.Utils;

import com.fashiontrunk.fashiontrunkapi.Util.ModelStatusMessage;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ModelStatusMessageTest {

    @Test
    void testModelStatusMessageCreationAndGetters() {
        UUID modelId = UUID.randomUUID();
        String status = "loaded";

        ModelStatusMessage message = new ModelStatusMessage(modelId, status);

        assertEquals(modelId, message.getModelId());
        assertEquals(status, message.getStatus());
    }

    @Test
    void testModelStatusMessageSetters() {
        ModelStatusMessage message = new ModelStatusMessage(UUID.randomUUID(), "initial");

        UUID newModelId = UUID.randomUUID();
        String newStatus = "completed";

        message.setModelId(newModelId);
        message.setStatus(newStatus);

        assertEquals(newModelId, message.getModelId());
        assertEquals(newStatus, message.getStatus());
    }
}
