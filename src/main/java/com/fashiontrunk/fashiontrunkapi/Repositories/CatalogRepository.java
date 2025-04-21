package com.fashiontrunk.fashiontrunkapi.Repositories;

import com.fashiontrunk.fashiontrunkapi.Models.CatalogEntity;
import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CatalogRepository extends JpaRepository<CatalogEntity, UUID> {

    Optional<CatalogEntity> findByNameAndParentIdAndOwner(String name, UUID parentId, UserEntity owner);

    List<CatalogEntity> findByParentId(UUID parentId);

    List<CatalogEntity> findByParentIsNullAndOwner(UserEntity owner);
}