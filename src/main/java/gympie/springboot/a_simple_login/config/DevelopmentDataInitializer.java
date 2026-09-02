package gympie.springboot.a_simple_login.config;

import gympie.springboot.a_simple_login.entity.AppUser;
import gympie.springboot.a_simple_login.entity.Role;
import gympie.springboot.a_simple_login.repository.AppUserRepository;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DevelopmentDataInitializer {

    @Bean
    CommandLineRunner seedAdminAccount(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        return arguments -> {
            if (!userRepository.existsByUsernameIgnoreCase("admin")) {
                userRepository.save(new AppUser("admin", "admin@example.test", "Demo administrator",
                        passwordEncoder.encode("change-me"), Set.of(Role.ADMIN, Role.USER)));
            }
        };
    }
}
