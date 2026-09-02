package gympie.springboot.a_simple_login.service;

import gympie.springboot.a_simple_login.entity.AppUser;
import gympie.springboot.a_simple_login.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAccountDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public UserAccountDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown username."));
        String[] authorities = user.getRoles().stream().map(role -> "ROLE_" + role.name()).toArray(String[]::new);
        return User.withUsername(user.getUsername()).password(user.getPasswordHash()).authorities(authorities).build();
    }
}
