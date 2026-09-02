package gympie.springboot.a_simple_login.dto;

import gympie.springboot.a_simple_login.entity.Role;
import java.util.Set;

public record UserAccountResponse(Long id, String username, String displayName, String email, Set<Role> roles) {
}
