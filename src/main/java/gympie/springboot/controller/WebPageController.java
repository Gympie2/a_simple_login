package gympie.springboot.controller;

import gympie.springboot.dto.RegistrationForm;
import gympie.springboot.exception.ResourceConflictException;
import gympie.springboot.service.UserAccountService;
import jakarta.validation.Valid;
import java.util.Objects;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebPageController {

    private final UserAccountService userAccountService;

    public WebPageController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @ModelAttribute
    void currentUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean signedIn = authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        model.addAttribute("currentUsername", signedIn ? authentication.getName() : null);
    }

    @GetMapping("/")
    String menu() {
        return "menu";
    }

    @GetMapping("/login")
    String login() {
        return "login";
    }

    @GetMapping("/signup")
    String signup(Model model) {
        if (!model.containsAttribute("registration")) {
            model.addAttribute("registration", new RegistrationForm());
        }
        return "signup";
    }

    @PostMapping("/signup")
    String register(@Valid @ModelAttribute("registration") RegistrationForm form, BindingResult bindingResult) {
        if (!Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match.");
        }
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        try {
            userAccountService.register(form);
        } catch (ResourceConflictException exception) {
            bindingResult.reject("registration", exception.getMessage());
            return "signup";
        }
        return "redirect:/login?registered";
    }

    @GetMapping("/error/access-denied")
    String accessDenied() {
        return "error/access-denied";
    }
}
