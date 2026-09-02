package gympie.springboot.a_simple_login.dto;

import gympie.springboot.a_simple_login.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record AdminUserUpdateRequest(
        @NotBlank @Size(max = 100) String displayName,
        @NotBlank @Email @Size(max = 254) String email,
        @Size(min = 8, max = 100) String replacementPassword,
        @NotEmpty Set<Role> roles) {
}
