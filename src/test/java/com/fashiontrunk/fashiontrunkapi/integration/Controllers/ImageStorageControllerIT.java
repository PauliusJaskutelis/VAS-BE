package com.fashiontrunk.fashiontrunkapi.integration.Controllers;

import com.fashiontrunk.fashiontrunkapi.Models.CatalogEntity;
import com.fashiontrunk.fashiontrunkapi.Repositories.CatalogRepository;
import com.fashiontrunk.fashiontrunkapi.Repositories.ImageRepository;
import com.fashiontrunk.fashiontrunkapi.Util.JwtAuthenticationFilter;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // kad duomenys po kiekvieno testo būtų ištrinti
class ImageStorageControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private ImageRepository imageRepository;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UUID catalogId;

    @BeforeEach
    void setup() {
        // Sukuriam katalogą testui
        CatalogEntity catalog = new CatalogEntity();
        catalog.setId(UUID.randomUUID());
        catalog.setName("Test Catalog");
        catalog.setPublic(true);
        catalogRepository.save(catalog);
        catalogId = catalog.getId();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void uploadImage_shouldReturnOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test.png",
                "image/png",
                new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A}
        );

        mockMvc.perform(multipart("/api/images/upload")
                        .file(file)
                        .param("catalogId", catalogId.toString())
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getImagesFromCatalog_shouldReturnOk() throws Exception {
        uploadImage_shouldReturnOk(); // Įkeliame pirmą failą

        mockMvc.perform(get("/api/images/catalog/{catalogId}", catalogId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void downloadImage_shouldReturnOk() throws Exception {
        uploadImage_shouldReturnOk(); // Įkeliame failą
        UUID imageId = imageRepository.findAll().get(0).getId();

        mockMvc.perform(get("/api/images/{imageId}", imageId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void deleteImage_shouldReturnOk() throws Exception {
        uploadImage_shouldReturnOk(); // Įkeliame failą
        UUID imageId = imageRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/images/{imageId}", imageId)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
