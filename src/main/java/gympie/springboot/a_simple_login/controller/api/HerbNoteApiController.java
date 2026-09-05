package gympie.springboot.a_simple_login.controller.api;

import gympie.springboot.a_simple_login.dto.HerbNoteForm;
import gympie.springboot.a_simple_login.dto.HerbNoteResponse;
import gympie.springboot.a_simple_login.service.HerbNoteService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
public class HerbNoteApiController {

    private final HerbNoteService herbNoteService;

    public HerbNoteApiController(HerbNoteService herbNoteService) {
        this.herbNoteService = herbNoteService;
    }

    @GetMapping
    List<HerbNoteResponse> findAll(Authentication authentication) {
        return isAdministrator(authentication) ? herbNoteService.findAll()
                : herbNoteService.findForUser(authentication.getName());
    }

    @GetMapping("/{id}")
    HerbNoteResponse findById(@PathVariable Long id, Authentication authentication) {
        return herbNoteService.findById(id, authentication.getName(), isAdministrator(authentication));
    }

    @PostMapping
    ResponseEntity<HerbNoteResponse> create(@Valid @RequestBody HerbNoteForm form, Authentication authentication) {
        HerbNoteResponse created = herbNoteService.create(form, authentication.getName());
        return ResponseEntity.created(URI.create("/api/notes/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    HerbNoteResponse update(@PathVariable Long id, @Valid @RequestBody HerbNoteForm form, Authentication authentication) {
        return herbNoteService.update(id, form, authentication.getName(), isAdministrator(authentication));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        herbNoteService.delete(id, authentication.getName(), isAdministrator(authentication));
        return ResponseEntity.noContent().build();
    }

    private boolean isAdministrator(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
