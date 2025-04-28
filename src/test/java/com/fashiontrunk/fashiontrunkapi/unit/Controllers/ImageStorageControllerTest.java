package com.fashiontrunk.fashiontrunkapi.unit.Controllers;

import com.fashiontrunk.fashiontrunkapi.Controllers.ImageStorageController;
import com.fashiontrunk.fashiontrunkapi.Models.ImageEntity;
import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Services.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageStorageController.class)
public class ImageStorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

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
void uploadImages_success() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
            "files",
            "image.png",
            "image/png",
            new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47}
    );

    UUID catalogId = UUID.randomUUID();

    when(imageService.uploadImagesToCatalog(eq(catalogId), eq(user.getId()), anyList()))
            .thenReturn(Collections.emptyList());

    mockMvc.perform(multipart("/api/images/upload")
                    .file(file)
                    .param("catalogId", catalogId.toString())
                    .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication(user)))
                    .with(SecurityMockMvcRequestPostProcessors.csrf()))
            .andExpect(status().isOk());
}

    @Test
    void getImagesFromCatalog_success() throws Exception {
        when(imageService.getImagesFromCatalog(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/images/catalog/" + UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication(user))))
                .andExpect(status().isOk());
    }

    @Test
    void downloadImage_success() throws Exception {
        ImageEntity image = new ImageEntity();
        image.setId(UUID.randomUUID());
        image.setFilename("test.png");
        image.setContentType("image/png");
        image.setData("test".getBytes());
        UserEntity owner = new UserEntity();
        owner.setId(user.getId());
        image.setOwner(owner);

        when(imageService.getImageById(any())).thenReturn(image);

        mockMvc.perform(get("/api/images/" + UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication(user))))
                .andExpect(status().isOk());
    }

    @Test
    void downloadImage_forbiddenWhenNotOwner() throws Exception {
        ImageEntity image = new ImageEntity();
        image.setId(UUID.randomUUID());
        UserEntity owner = new UserEntity();
        owner.setId(UUID.randomUUID());
        image.setOwner(owner);

        when(imageService.getImageById(any())).thenReturn(image);

        mockMvc.perform(get("/api/images/" + UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication(user))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteImage_success() throws Exception {
        UUID imageId = UUID.randomUUID();
    
        doNothing().when(imageService).deleteImage(eq(imageId));
    
        mockMvc.perform(delete("/api/images/" + imageId)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(mockAuthentication(user)))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }
}
