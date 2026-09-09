package gympie.springboot.controller.api;

import gympie.springboot.dto.CategoryForm;
import gympie.springboot.dto.CategoryResponse;
import gympie.springboot.service.CatalogueService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryApiController {

    private final CatalogueService catalogueService;

    public CategoryApiController(CatalogueService catalogueService) {
        this.catalogueService = catalogueService;
    }

    @GetMapping
    List<CategoryResponse> findAll() {
        return catalogueService.findCategories();
    }

    @GetMapping("/{id}")
    CategoryResponse findById(@PathVariable Long id) {
        return catalogueService.findCategoryById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryForm form) {
        CategoryResponse created = catalogueService.createCategory(form);
        return ResponseEntity.created(URI.create("/api/categories/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryForm form) {
        return catalogueService.updateCategory(id, form);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> delete(@PathVariable Long id) {
        catalogueService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
