package com.fashiontrunk.fashiontrunkapi.unit.Utils;

import com.fashiontrunk.fashiontrunkapi.Dto.ImageDTO;
import com.fashiontrunk.fashiontrunkapi.Models.CatalogEntity;
import com.fashiontrunk.fashiontrunkapi.Models.ImageEntity;
import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Util.ImageMapper;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ImageMapperTest {

    @Test
    void testToDTO() {
        UUID imageId = UUID.randomUUID();
        UUID catalogId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        ImageEntity image = new ImageEntity();
        image.setId(imageId);
        image.setFilename("test.jpg");
        image.setContentType("image/jpeg");
        image.setFormat("jpeg");
        image.setColorMode("RGB");
        image.setWidth(100);
        image.setHeight(200);
        image.setUploadDate(new Date());

        CatalogEntity catalog = new CatalogEntity();
        catalog.setId(catalogId);
        image.setCatalog(catalog);

        UserEntity owner = new UserEntity();
        owner.setId(ownerId);
        image.setOwner(owner);

        ImageDTO dto = ImageMapper.toDTO(image);

        assertEquals(imageId, dto.getId());
        assertEquals("test.jpg", dto.getFilename());
        assertEquals("image/jpeg", dto.getContentType());
        assertEquals("jpeg", dto.getFormat());
        assertEquals("RGB", dto.getColorMode());
        assertEquals(100, dto.getWidth());
        assertEquals(200, dto.getHeight());
        assertEquals(catalogId, dto.getCatalogId());
        assertEquals(ownerId, dto.getOwnerId());
    }
}
