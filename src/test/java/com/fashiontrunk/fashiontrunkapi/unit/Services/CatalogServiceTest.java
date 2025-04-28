package com.fashiontrunk.fashiontrunkapi.unit.Services;

import com.fashiontrunk.fashiontrunkapi.Dto.CatalogDTO;
import com.fashiontrunk.fashiontrunkapi.Models.CatalogEntity;
import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Repositories.CatalogRepository;
import com.fashiontrunk.fashiontrunkapi.Services.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CatalogServiceTest {

    private CatalogRepository catalogRepository;
    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        catalogRepository = mock(CatalogRepository.class);
        catalogService = new CatalogService(catalogRepository);
    }

    @Test
    void getRootCatalogsForUser_returnsCatalogDTOs() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());

        CatalogEntity catalog = new CatalogEntity();
        catalog.setId(UUID.randomUUID());
        catalog.setName("Root Catalog");
        catalog.setOwner(user);

        when(catalogRepository.findByParentIsNullAndOwner(user)).thenReturn(List.of(catalog));

        List<CatalogDTO> result = catalogService.getRootCatalogsForUser(user);

        assertEquals(1, result.size());
        assertEquals("Root Catalog", result.get(0).getName());
    }

    @Test
    void createCatalog_createsNewCatalog() {
        UserEntity owner = new UserEntity();
        owner.setId(UUID.randomUUID());

        when(catalogRepository.findByNameAndParentIdAndOwner("New Catalog", null, owner))
                .thenReturn(Optional.empty());
        when(catalogRepository.save(any(CatalogEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CatalogDTO catalogDTO = catalogService.createCatalog("New Catalog", owner, null, false);

        assertEquals("New Catalog", catalogDTO.getName());
        assertNull(catalogDTO.getParentId());
    }

    @Test
    void createCatalog_throwsException_ifNameExists() {
        UserEntity owner = new UserEntity();
        owner.setId(UUID.randomUUID());

        when(catalogRepository.findByNameAndParentIdAndOwner("Existing Catalog", null, owner))
                .thenReturn(Optional.of(new CatalogEntity()));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                catalogService.createCatalog("Existing Catalog", owner, null, false));

        assertEquals("Catalog with same name already exists in this folder.", exception.getMessage());
    }

    @Test
    void deleteCatalog_successfulDeletion() throws IllegalAccessException {
        UserEntity owner = new UserEntity();
        owner.setId(UUID.randomUUID());

        CatalogEntity catalog = new CatalogEntity();
        catalog.setId(UUID.randomUUID());
        catalog.setOwner(owner);

        when(catalogRepository.findById(catalog.getId())).thenReturn(Optional.of(catalog));

        catalogService.deleteCatalog(catalog.getId(), owner);

        verify(catalogRepository).delete(catalog);
    }

    @Test
    void deleteCatalog_throwsIllegalAccessException_whenUserNotOwner() {
        UserEntity owner = new UserEntity();
        owner.setId(UUID.randomUUID());

        UserEntity anotherUser = new UserEntity();
        anotherUser.setId(UUID.randomUUID());

        CatalogEntity catalog = new CatalogEntity();
        catalog.setId(UUID.randomUUID());
        catalog.setOwner(owner);

        when(catalogRepository.findById(catalog.getId())).thenReturn(Optional.of(catalog));

        assertThrows(IllegalAccessException.class, () ->
                catalogService.deleteCatalog(catalog.getId(), anotherUser));
    }

    @Test
void getChildren_filtersByOwner() {
    UserEntity owner = new UserEntity();
    owner.setId(UUID.randomUUID());

    CatalogEntity child1 = new CatalogEntity();
    child1.setId(UUID.randomUUID());
    child1.setName("Child 1");
    child1.setOwner(owner);

    UserEntity anotherOwner = new UserEntity(); // FIX: Create different owner
    anotherOwner.setId(UUID.randomUUID());       // FIX: Set ID for second owner

    CatalogEntity child2 = new CatalogEntity();
    child2.setId(UUID.randomUUID());
    child2.setName("Child 2");
    child2.setOwner(anotherOwner); // Different owner

    when(catalogRepository.findByParentId(any())).thenReturn(List.of(child1, child2));

    List<CatalogDTO> result = catalogService.getChildren(UUID.randomUUID(), owner);

    assertEquals(1, result.size());
    assertEquals("Child 1", result.get(0).getName());
}
}
