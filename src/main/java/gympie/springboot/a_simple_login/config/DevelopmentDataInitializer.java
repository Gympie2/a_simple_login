package gympie.springboot.a_simple_login.config;

import gympie.springboot.a_simple_login.entity.AppUser;
import gympie.springboot.a_simple_login.entity.Herb;
import gympie.springboot.a_simple_login.entity.HerbCategory;
import gympie.springboot.a_simple_login.entity.Role;
import gympie.springboot.a_simple_login.repository.AppUserRepository;
import gympie.springboot.a_simple_login.repository.HerbCategoryRepository;
import gympie.springboot.a_simple_login.repository.HerbRepository;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DevelopmentDataInitializer {

    private static final String DEMO_ADMIN_PASSWORD = "admin-access2222";

    @Bean
    CommandLineRunner seedDevelopmentData(AppUserRepository userRepository, HerbCategoryRepository categoryRepository,
                                         HerbRepository herbRepository, PasswordEncoder passwordEncoder) {
        return arguments -> {
            userRepository.findByUsername("admin").ifPresentOrElse(
                    administrator -> {
                        // Keep the local demonstration account predictable after a restart.
                        administrator.updatePassword(passwordEncoder.encode(DEMO_ADMIN_PASSWORD));
                        userRepository.save(administrator);
                    },
                    () -> userRepository.save(new AppUser("admin", "admin@example.test", "Demo administrator",
                            passwordEncoder.encode(DEMO_ADMIN_PASSWORD), Set.of(Role.ADMIN, Role.USER))));
            if (categoryRepository.count() == 0) {
                HerbCategory calming = categoryRepository.save(new HerbCategory("Calming herbs",
                        "Plants commonly included in gentle evening infusions."));
                HerbCategory culinary = categoryRepository.save(new HerbCategory("Culinary herbs",
                        "Everyday herbs used for flavour and simple kitchen preparations."));
                HerbCategory aromatic = categoryRepository.save(new HerbCategory("Aromatic herbs",
                        "Fragrant plants traditionally used in sachets and baths."));
                herbRepository.save(new Herb("Chamomile", "Matricaria chamomilla",
                        "A small flowering herb often prepared as a mild evening tea.", calming));
                herbRepository.save(new Herb("Lemon balm", "Melissa officinalis",
                        "A lemon-scented leaf used in infusions and garden borders.", calming));
                herbRepository.save(new Herb("Mint", "Mentha spicata",
                        "A fresh-tasting leaf used in teas, salads, and cold drinks.", culinary));
                herbRepository.save(new Herb("Lavender", "Lavandula angustifolia",
                        "A fragrant flowering plant often kept for dried bundles and sachets.", aromatic));
            }
        };
    }
}
