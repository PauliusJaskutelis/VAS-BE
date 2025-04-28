package com.fashiontrunk.fashiontrunkapi.Controllers;

import com.fashiontrunk.fashiontrunkapi.Dto.CatalogDTO;
import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Services.CatalogService;
import com.fashiontrunk.fashiontrunkapi.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalogs")
public class CatalogController {

    private final CatalogService catalogService;
    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/root")
    public ResponseEntity<List<CatalogDTO>> getRootCatalogs(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();

        List<CatalogDTO> rootCatalogs = catalogService.getRootCatalogsForUser(user);
        return ResponseEntity.ok(rootCatalogs);
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<CatalogDTO>> getChildren(@PathVariable UUID parentId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();

        List<CatalogDTO> children = catalogService.getChildren(parentId, user);
        return ResponseEntity.ok(children);
    }

    @PostMapping
    public ResponseEntity<?> createCatalog(
            @RequestParam String name,
            @RequestParam(required = false) UUID parentId,
            @RequestParam(defaultValue = "false") boolean isPublic,
            Authentication authentication
    ) {
        try {
            UserEntity user = (UserEntity) authentication.getPrincipal();
            CatalogDTO created = catalogService.createCatalog(name, user, parentId, isPublic);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCatalog(@PathVariable UUID id, Authentication authentication) {
        try {
            UserEntity user = (UserEntity) authentication.getPrincipal();
            catalogService.deleteCatalog(id, user); // validate user owns the catalog
            return ResponseEntity.ok().build();
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(403).body("Access denied");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
