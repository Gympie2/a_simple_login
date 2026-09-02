package gympie.springboot.a_simple_login.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gympie.springboot.a_simple_login.dto.RegistrationForm;
import gympie.springboot.a_simple_login.entity.AppUser;
import gympie.springboot.a_simple_login.entity.Role;
import gympie.springboot.a_simple_login.exception.ResourceConflictException;
import gympie.springboot.a_simple_login.repository.AppUserRepository;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTests {

    @Mock
    private AppUserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserAccountService userAccountService;

    @Test
    void registrationEncryptsPasswordAndAssignsUserRole() {
        RegistrationForm form = form("garden-reader", "reader@example.test");
        when(passwordEncoder.encode("long-enough-password")).thenReturn("encoded-password");
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userAccountService.register(form);

        ArgumentCaptor<AppUser> saved = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getPasswordHash()).isEqualTo("encoded-password");
        assertThat(saved.getValue().getRoles()).containsExactly(Role.USER);
    }

    @Test
    void registrationRejectsAnExistingUsername() {
        RegistrationForm form = form("garden-reader", "reader@example.test");
        when(userRepository.existsByUsernameIgnoreCase("garden-reader")).thenReturn(true);

        assertThatThrownBy(() -> userAccountService.register(form))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("username");
    }

    private RegistrationForm form(String username, String email) {
        RegistrationForm form = new RegistrationForm();
        form.setDisplayName("Garden Reader");
        form.setUsername(username);
        form.setEmail(email);
        form.setPassword("long-enough-password");
        form.setConfirmPassword("long-enough-password");
        return form;
    }
}
