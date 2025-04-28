package com.fashiontrunk.fashiontrunkapi.unit.Services;

import com.fashiontrunk.fashiontrunkapi.Models.ModelEntity;
import com.fashiontrunk.fashiontrunkapi.Repositories.ModelRepository;
import com.fashiontrunk.fashiontrunkapi.Services.ModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModelServiceTest {

    private ModelRepository modelRepository;
    private ModelService modelService;

    @BeforeEach
    void setUp() {
        modelRepository = mock(ModelRepository.class);
        modelService = new ModelService(modelRepository);
    }

    @Test
    void saveModel_savesModelSuccessfully() {
        ModelEntity model = new ModelEntity();
        when(modelRepository.save(model)).thenReturn(model);

        ModelEntity saved = modelService.saveModel(model);

        assertEquals(model, saved);
        verify(modelRepository).save(model);
    }

    @Test
    void getModelById_returnsModelWhenExists() {
        UUID id = UUID.randomUUID();
        ModelEntity model = new ModelEntity();
        when(modelRepository.findById(id)).thenReturn(Optional.of(model));

        Optional<ModelEntity> found = modelService.getModelById(id);

        assertTrue(found.isPresent());
        assertEquals(model, found.get());
    }

    @Test
    void getAllModels_returnsAllModels() {
        List<ModelEntity> models = Arrays.asList(new ModelEntity(), new ModelEntity());
        when(modelRepository.findAll()).thenReturn(models);

        List<ModelEntity> all = modelService.getAllModels();

        assertEquals(2, all.size());
        verify(modelRepository).findAll();
    }
}
