package com.fashiontrunk.fashiontrunkapi.integration.Controllers;

import com.fashiontrunk.fashiontrunkapi.Models.CatalogEntity;
import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Repositories.CatalogRepository;
import com.fashiontrunk.fashiontrunkapi.Repositories.UserRepository;
import com.fashiontrunk.fashiontrunkapi.integration.Config.TestSecurityConfig;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@Transactional
class CatalogControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CatalogRepository catalogRepository;

    @MockBean
    private UserRepository userRepository;

    private UUID parentCatalogId;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        // Sukuriam vartotoją su ID
        testUser = new UserEntity();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("testuser@test.com");

        // Sukuriam root katalogą
        CatalogEntity rootCatalog = new CatalogEntity();
        rootCatalog.setName("Root Catalog");
        rootCatalog.setPublic(true);
        rootCatalog.setOwner(testUser);

        CatalogEntity savedRoot = catalogRepository.save(rootCatalog);
        parentCatalogId = savedRoot.getId();

        // Sukuriam vaikinį katalogą
        CatalogEntity childCatalog = new CatalogEntity();
        childCatalog.setName("Child Catalog");
        childCatalog.setPublic(true);
        childCatalog.setOwner(testUser);
        childCatalog.setParent(savedRoot);

        catalogRepository.save(childCatalog);
    }

    @Test
    @WithMockUser(username = "testuser@test.com", roles = {"USER"})
    void getRootCatalogs_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/catalogs/root")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name", is("Root Catalog")));
    }

    @Test
    @WithMockUser(username = "testuser@test.com", roles = {"USER"})
    void getChildren_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/catalogs/{parentId}/children", parentCatalogId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name", is("Child Catalog")));
    }

    @Test
    @WithMockUser(username = "testuser@test.com", roles = {"USER"})
    void createCatalog_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/catalogs")
                        .param("name", "New Catalog")
                        .param("parentId", parentCatalogId.toString())
                        .param("isPublic", "true")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New Catalog")));
    }

    @Test
    @WithMockUser(username = "testuser@test.com", roles = {"USER"})
    void deleteCatalog_shouldReturnOk() throws Exception {
        CatalogEntity tempCatalog = new CatalogEntity();
        tempCatalog.setName("Temp Catalog");
        tempCatalog.setPublic(true);
        tempCatalog.setOwner(testUser);

        CatalogEntity saved = catalogRepository.save(tempCatalog);

        mockMvc.perform(delete("/api/catalogs/{id}", saved.getId())
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
