package gympie.springboot.a_simple_login.controller.api;

import gympie.springboot.a_simple_login.dto.AdminUserCreateRequest;
import gympie.springboot.a_simple_login.dto.AdminUserUpdateRequest;
import gympie.springboot.a_simple_login.dto.UserAccountResponse;
import gympie.springboot.a_simple_login.service.UserAccountService;
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

/** Administrator-only JSON CRUD for the one resource in this focused project: accounts. */
@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final UserAccountService userAccountService;

    public UserApiController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping
    List<UserAccountResponse> findAll() {
        return userAccountService.findAll();
    }

    @GetMapping("/{id}")
    UserAccountResponse findById(@PathVariable Long id) {
        return userAccountService.findById(id);
    }

    @PostMapping
    ResponseEntity<UserAccountResponse> create(@Valid @RequestBody AdminUserCreateRequest request) {
        UserAccountResponse created = userAccountService.create(request);
        return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    UserAccountResponse update(@PathVariable Long id, @Valid @RequestBody AdminUserUpdateRequest request,
                               Authentication authentication) {
        return userAccountService.update(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        userAccountService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
