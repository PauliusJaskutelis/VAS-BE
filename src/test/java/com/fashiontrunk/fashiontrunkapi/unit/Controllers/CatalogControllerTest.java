package com.fashiontrunk.fashiontrunkapi.unit.Controllers;

import com.fashiontrunk.fashiontrunkapi.Controllers.CatalogController;
import com.fashiontrunk.fashiontrunkapi.Dto.CatalogDTO;
import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Services.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;



import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@WebMvcTest(CatalogController.class)
public class CatalogControllerTest {

    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogService catalogService;

    @MockBean
    private Authentication authentication;

    private UserEntity mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setId(UUID.randomUUID());
    }

    @Test
    void testGetRootCatalogs() throws Exception {
        // Sukuri testinį userį
        UserEntity testUser = new UserEntity();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
    
        // Sukuri Authentication
        Authentication auth = new UsernamePasswordAuthenticationToken(testUser, null, new ArrayList<>());
    
        // Mockini service
        when(catalogService.getRootCatalogsForUser(any())).thenReturn(Collections.singletonList(
            new CatalogDTO(UUID.randomUUID(), "root", null)
        ));
    
        // Paleidi testą su authentication
        mockMvc.perform(MockMvcRequestBuilders.get("/api/catalogs/root")
                .with(authentication(auth)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    
        // Tikrini kad buvo iškviestas
        verify(catalogService, times(1)).getRootCatalogsForUser(any(UserEntity.class));
    }
    @Test
    void testGetChildren() throws Exception {
        UserEntity testUser = new UserEntity();
        UUID parentId = UUID.randomUUID();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        Authentication auth = new UsernamePasswordAuthenticationToken(testUser, null, new ArrayList<>());

        when(catalogService.getChildren(eq(parentId), any())).thenReturn(Collections.singletonList(
            new CatalogDTO(UUID.randomUUID(), "child", parentId)
            ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/catalogs/" + parentId + "/children")
                .with(authentication(auth)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(catalogService, times(1)).getChildren(eq(parentId), any(UserEntity.class));
    }
}
