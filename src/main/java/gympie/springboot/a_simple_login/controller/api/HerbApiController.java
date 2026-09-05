package gympie.springboot.a_simple_login.controller.api;

import gympie.springboot.a_simple_login.dto.HerbForm;
import gympie.springboot.a_simple_login.dto.HerbResponse;
import gympie.springboot.a_simple_login.service.CatalogueService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/herbs")
public class HerbApiController {

    private final CatalogueService catalogueService;

    public HerbApiController(CatalogueService catalogueService) {
        this.catalogueService = catalogueService;
    }

    @GetMapping
    List<HerbResponse> findAll(@RequestParam(required = false) String query,
                               @RequestParam(required = false) Long categoryId) {
        return catalogueService.findHerbs(query, categoryId);
    }

    @GetMapping("/{id}")
    HerbResponse findById(@PathVariable Long id) {
        return catalogueService.findHerbById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<HerbResponse> create(@Valid @RequestBody HerbForm form) {
        HerbResponse created = catalogueService.createHerb(form);
        return ResponseEntity.created(URI.create("/api/herbs/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    HerbResponse update(@PathVariable Long id, @Valid @RequestBody HerbForm form) {
        return catalogueService.updateHerb(id, form);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> delete(@PathVariable Long id) {
        catalogueService.deleteHerb(id);
        return ResponseEntity.noContent().build();
    }
}
