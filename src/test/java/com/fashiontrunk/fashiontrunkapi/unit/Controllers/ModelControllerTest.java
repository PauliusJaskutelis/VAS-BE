package com.fashiontrunk.fashiontrunkapi.unit.Controllers;

import com.fashiontrunk.fashiontrunkapi.Controllers.ModelController;
import com.fashiontrunk.fashiontrunkapi.Models.ModelEntity;
import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Services.MetadataService;
import com.fashiontrunk.fashiontrunkapi.Services.ModelService;
import com.fashiontrunk.fashiontrunkapi.Util.ModelStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(ModelController.class)
public class ModelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModelService modelService;

    @MockBean
    private MetadataService metadataService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @MockBean
    private ModelStorage modelStorage;
    
    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(UUID.randomUUID());
    }

    private Authentication mockAuthentication(UserEntity user) {
        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void getModelById_ModelExists_ReturnsModel() throws Exception {
        UUID id = UUID.randomUUID();
        ModelEntity model = new ModelEntity();
        model.setId(id);

        when(modelService.getModelById(id)).thenReturn(Optional.of(model));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/models/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication(user))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getModelById_ModelDoesNotExist_ReturnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(modelService.getModelById(id)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/models/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication(user))))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getAllModels_ReturnsList() throws Exception {
        when(modelService.getAllModels()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/models")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication(user))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
