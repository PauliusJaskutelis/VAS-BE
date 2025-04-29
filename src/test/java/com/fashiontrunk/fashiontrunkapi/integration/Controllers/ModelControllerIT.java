package com.fashiontrunk.fashiontrunkapi.integration.Controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ModelControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "adminuser", roles = {"ADMIN"})
    void uploadModel_shouldReturnOk() throws Exception {
        MockMultipartFile modelFile = new MockMultipartFile(
                "file",
                "model.h5",
                "application/octet-stream",
                "fake-model-content".getBytes()
        );

        mockMvc.perform(multipart("/api/models/upload")
                        .file(modelFile)
                        .param("name", "TestModel")
                        .param("description", "Test model description")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }
}
