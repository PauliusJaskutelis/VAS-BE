package com.fashiontrunk.fashiontrunkapi.Util;

import com.fashiontrunk.fashiontrunkapi.Dto.ImageDTO;
import com.fashiontrunk.fashiontrunkapi.Models.ImageEntity;

public class ImageMapper {
    public static ImageDTO toDTO(ImageEntity image) {
        ImageDTO dto = new ImageDTO();
        dto.setId(image.getId());
        dto.setFilename(image.getFilename());
        dto.setContentType(image.getContentType());
        dto.setFormat(image.getFormat());
        dto.setColorMode(image.getColorMode());
        dto.setWidth(image.getWidth());
        dto.setHeight(image.getHeight());
        dto.setUploadDate(image.getUploadDate());
        dto.setCatalogId(image.getCatalog().getId());
        dto.setOwnerId(image.getOwner().getId());
        return dto;
    }
}