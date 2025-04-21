package com.fashiontrunk.fashiontrunkapi.Services;

import com.fashiontrunk.fashiontrunkapi.Models.CatalogEntity;
import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Repositories.CatalogRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CatalogService {

    private final CatalogRepository catalogRepository;

    public CatalogService(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public List<CatalogEntity> getRootCatalogsForUser(UserEntity user) {
        return catalogRepository.findByParentIsNullAndOwner(user);
    }

    public List<CatalogEntity> getChildren(UUID parentId) {
        return catalogRepository.findByParentId(parentId);
    }

    @Transactional
    public CatalogEntity createCatalog(String name, UserEntity owner, UUID parentId, boolean isPublic) {
        CatalogEntity parent = null;
        if (parentId != null) {
            parent = catalogRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent catalog not found"));
        }

        boolean exists = catalogRepository.findByNameAndParentIdAndOwner(name, parentId, owner).isPresent();
        if (exists) {
            throw new RuntimeException("Catalog with same name already exists in this folder.");
        }

        CatalogEntity catalog = new CatalogEntity();
        catalog.setName(name);
        catalog.setOwner(owner);
        catalog.setParent(parent);
        catalog.setPublic(isPublic);
        return catalogRepository.save(catalog);
    }

    public List<CatalogEntity> getBreadcrumb(UUID catalogId) {
        List<CatalogEntity> breadcrumb = new ArrayList<>();
        Optional<CatalogEntity> current = catalogRepository.findById(catalogId);
        while (current.isPresent()) {
            breadcrumb.add(0, current.get()); // prepend to keep order from root
            current = Optional.ofNullable(current.get().getParent());
        }
        return breadcrumb;
    }

    public Optional<CatalogEntity> getCatalog(UUID id) {
        return catalogRepository.findById(id);
    }

    @Transactional
    public void deleteCatalog(UUID catalogId) {
        CatalogEntity catalog = catalogRepository.findById(catalogId)
                .orElseThrow(() -> new RuntimeException("Catalog not found"));

        // Pašaliname rekursyviai visus vidinius katalogus
        deleteChildrenRecursive(catalog);

        // Galiausiai pašalinam šį katalogą
        catalogRepository.delete(catalog);
    }

    private void deleteChildrenRecursive(CatalogEntity parent) {
        List<CatalogEntity> children = catalogRepository.findByParentId(parent.getId());
        for (CatalogEntity child : children) {
            deleteChildrenRecursive(child);
        }
        catalogRepository.deleteAll(children);
    }
}