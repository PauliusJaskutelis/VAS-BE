package com.fashiontrunk.fashiontrunkapi.Repositories;

import com.fashiontrunk.fashiontrunkapi.Models.ImageEntity;
import com.fashiontrunk.fashiontrunkapi.Models.CatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<ImageEntity, UUID> {

    List<ImageEntity> findByCatalog(CatalogEntity catalog);
}