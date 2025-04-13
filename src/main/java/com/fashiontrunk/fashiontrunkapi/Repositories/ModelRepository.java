package com.fashiontrunk.fashiontrunkapi.Repositories;

import com.fashiontrunk.fashiontrunkapi.Models.ModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ModelRepository extends JpaRepository<ModelEntity, UUID> {
    //Custom queries
}
