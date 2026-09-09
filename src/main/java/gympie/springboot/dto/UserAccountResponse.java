package gympie.springboot.dto;

import gympie.springboot.entity.Role;
import java.util.Set;

public record UserAccountResponse(Long id, String username, String displayName, String email, Set<Role> roles) {
}
