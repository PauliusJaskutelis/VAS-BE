package com.fashiontrunk.fashiontrunkapi.Controllers;

import com.fashiontrunk.fashiontrunkapi.Models.CatalogEntity;
import com.fashiontrunk.fashiontrunkapi.Models.UserEntity;
import com.fashiontrunk.fashiontrunkapi.Repositories.UserRepository;
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
    public ResponseEntity<List<CatalogEntity>> getRootCatalogs(@RequestParam("userId") UUID userId) {
        // In production, user info should come from SecurityContext
        UserEntity user = new UserEntity(); user.setId(userId);
        List<CatalogEntity> catalogs = catalogService.getRootCatalogsForUser(user);
        return ResponseEntity.ok(catalogs);
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<CatalogEntity>> getChildren(@PathVariable UUID parentId) {
        List<CatalogEntity> children = catalogService.getChildren(parentId);
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
            CatalogEntity created = catalogService.createCatalog(name, user, parentId, isPublic);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCatalog(@PathVariable UUID id) {
        try {
            catalogService.deleteCatalog(id);
            return ResponseEntity.ok("Catalog deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Catalog not found");
        }
    }
}
