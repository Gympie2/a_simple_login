package gympie.springboot.a_simple_login.service;

import gympie.springboot.a_simple_login.dto.AdminUserCreateRequest;
import gympie.springboot.a_simple_login.dto.AdminUserUpdateRequest;
import gympie.springboot.a_simple_login.dto.RegistrationForm;
import gympie.springboot.a_simple_login.dto.UserAccountResponse;
import gympie.springboot.a_simple_login.entity.AppUser;
import gympie.springboot.a_simple_login.entity.Role;
import gympie.springboot.a_simple_login.exception.ForbiddenOperationException;
import gympie.springboot.a_simple_login.exception.ResourceConflictException;
import gympie.springboot.a_simple_login.exception.ResourceNotFoundException;
import gympie.springboot.a_simple_login.repository.AppUserRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserAccountService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserAccountResponse> findAll() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(AppUser::getUsername, String.CASE_INSENSITIVE_ORDER))
                .map(this::toResponse)
                .toList();
    }

    public UserAccountResponse findById(Long id) {
        return toResponse(getUser(id));
    }

    @Transactional
    public UserAccountResponse register(RegistrationForm form) {
        requireAvailableIdentity(form.getUsername(), form.getEmail(), null);
        AppUser user = new AppUser(form.getUsername().trim(), form.getEmail().trim(), form.getDisplayName().trim(),
                passwordEncoder.encode(form.getPassword()), Set.of(Role.USER));
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserAccountResponse create(AdminUserCreateRequest request) {
        requireAvailableIdentity(request.username(), request.email(), null);
        AppUser user = new AppUser(request.username().trim(), request.email().trim(), request.displayName().trim(),
                passwordEncoder.encode(request.password()), Set.copyOf(request.roles()));
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserAccountResponse update(Long id, AdminUserUpdateRequest request, String actingUsername) {
        AppUser user = getUser(id);
        if (user.getUsername().equals(actingUsername) && !request.roles().contains(Role.ADMIN)) {
            throw new ForbiddenOperationException("You cannot remove your own administrator role.");
        }
        requireAvailableIdentity(user.getUsername(), request.email(), id);
        user.update(request.displayName().trim(), request.email().trim(), Set.copyOf(request.roles()));
        if (request.replacementPassword() != null && !request.replacementPassword().isBlank()) {
            user.updatePassword(passwordEncoder.encode(request.replacementPassword()));
        }
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id, String actingUsername) {
        AppUser user = getUser(id);
        if (user.getUsername().equals(actingUsername)) {
            throw new ForbiddenOperationException("You cannot delete the account currently in use.");
        }
        userRepository.delete(user);
    }

    private AppUser getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private void requireAvailableIdentity(String username, String email, Long id) {
        if (id == null && userRepository.existsByUsernameIgnoreCase(username.trim())) {
            throw new ResourceConflictException("That username is already in use.");
        }
        boolean emailInUse = id == null
                ? userRepository.existsByEmailIgnoreCase(email.trim())
                : userRepository.existsByEmailIgnoreCaseAndIdNot(email.trim(), id);
        if (emailInUse) {
            throw new ResourceConflictException("That email address is already in use.");
        }
    }

    private UserAccountResponse toResponse(AppUser user) {
        return new UserAccountResponse(user.getId(), user.getUsername(), user.getDisplayName(), user.getEmail(),
                Set.copyOf(user.getRoles()));
    }
}
