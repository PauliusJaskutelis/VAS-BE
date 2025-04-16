package com.fashiontrunk.fashiontrunkapi.Services;

import com.fashiontrunk.fashiontrunkapi.Models.ModelEntity;
import com.fashiontrunk.fashiontrunkapi.Repositories.ModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ModelService {
    private final ModelRepository modelRepository;

    @Autowired
    public ModelService(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    public ModelEntity saveModel(ModelEntity model) {
        return modelRepository.save(model);
    }

    public Optional<ModelEntity> getModelById(UUID id){
        return modelRepository.findById(id);
    }
    public List<ModelEntity> getAllModels() {
        return modelRepository.findAll();
    }
}
