package com.fashiontrunk.fashiontrunkapi.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatalogDTO {
    private UUID id;
    private String name;
    private UUID parentId;
    // Add other fields as needed (e.g., creationDate, etc.)
}