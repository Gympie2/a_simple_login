package gympie.springboot.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gympie.springboot.entity.AppUser;
import gympie.springboot.entity.Role;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AppUserRepositoryTests {

    @Autowired
    private AppUserRepository userRepository;

    @Test
    void findsPersistedUsersByUsernameAndChecksEmailUniqueness() {
        userRepository.saveAndFlush(new AppUser("garden-reader", "reader@example.test", "Garden Reader",
                "not-a-real-password", Set.of(Role.USER)));

        assertThat(userRepository.findByUsername("garden-reader")).isPresent();
        assertThat(userRepository.existsByEmailIgnoreCase("READER@example.test")).isTrue();
    }
}
