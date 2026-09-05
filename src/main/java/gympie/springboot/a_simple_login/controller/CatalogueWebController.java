package gympie.springboot.a_simple_login.controller;

import gympie.springboot.a_simple_login.dto.CategoryForm;
import gympie.springboot.a_simple_login.dto.CategoryResponse;
import gympie.springboot.a_simple_login.dto.HerbForm;
import gympie.springboot.a_simple_login.dto.HerbNoteForm;
import gympie.springboot.a_simple_login.dto.HerbNoteResponse;
import gympie.springboot.a_simple_login.dto.HerbResponse;
import gympie.springboot.a_simple_login.exception.ResourceConflictException;
import gympie.springboot.a_simple_login.service.CatalogueService;
import gympie.springboot.a_simple_login.service.HerbNoteService;
import gympie.springboot.a_simple_login.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CatalogueWebController {

    private final CatalogueService catalogueService;
    private final HerbNoteService herbNoteService;
    private final UserAccountService userAccountService;

    public CatalogueWebController(CatalogueService catalogueService, HerbNoteService herbNoteService,
                                 UserAccountService userAccountService) {
        this.catalogueService = catalogueService;
        this.herbNoteService = herbNoteService;
        this.userAccountService = userAccountService;
    }

    @ModelAttribute
    void currentUser(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean signedIn = authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        model.addAttribute("currentUsername", signedIn ? authentication.getName() : null);
        model.addAttribute("isAdmin", signedIn && isAdministrator(authentication));
    }

    @GetMapping("/catalogue")
    String catalogue(@RequestParam(required = false) String query, @RequestParam(required = false) Long categoryId,
                     Model model) {
        model.addAttribute("herbs", catalogueService.findHerbs(query, categoryId));
        model.addAttribute("categories", catalogueService.findCategories());
        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("selectedCategoryId", categoryId);
        return "catalogue";
    }

    @GetMapping("/catalogue/herbs/{id}")
    String herbDetail(@PathVariable Long id, Model model) {
        model.addAttribute("herb", catalogueService.findHerbById(id));
        return "herb-detail";
    }

    @GetMapping("/notes")
    String notes(Authentication authentication, Model model) {
        model.addAttribute("notes", herbNoteService.findForUser(authentication.getName()));
        return "notes";
    }

    @GetMapping("/notes/new")
    String newNote(@RequestParam(required = false) Long herbId, Model model) {
        HerbNoteForm form = new HerbNoteForm();
        form.setHerbId(herbId);
        model.addAttribute("noteForm", form);
        addHerbs(model);
        model.addAttribute("formTitle", "Add note");
        model.addAttribute("formAction", "/notes");
        return "note-form";
    }

    @PostMapping("/notes")
    String createNote(@Valid @ModelAttribute("noteForm") HerbNoteForm form, BindingResult bindingResult,
                      Authentication authentication, Model model) {
        if (bindingResult.hasErrors()) {
            addHerbs(model);
            model.addAttribute("formTitle", "Add note");
            model.addAttribute("formAction", "/notes");
            return "note-form";
        }
        herbNoteService.create(form, authentication.getName());
        return "redirect:/notes?created";
    }

    @GetMapping("/notes/{id}/edit")
    String editNote(@PathVariable Long id, Authentication authentication, Model model) {
        HerbNoteResponse note = herbNoteService.findById(id, authentication.getName(), isAdministrator(authentication));
        model.addAttribute("noteForm", noteForm(note));
        addHerbs(model);
        model.addAttribute("formTitle", "Edit note");
        model.addAttribute("formAction", "/notes/" + id);
        return "note-form";
    }

    @PostMapping("/notes/{id}")
    String updateNote(@PathVariable Long id, @Valid @ModelAttribute("noteForm") HerbNoteForm form,
                      BindingResult bindingResult, Authentication authentication, Model model) {
        if (bindingResult.hasErrors()) {
            addHerbs(model);
            model.addAttribute("formTitle", "Edit note");
            model.addAttribute("formAction", "/notes/" + id);
            return "note-form";
        }
        herbNoteService.update(id, form, authentication.getName(), isAdministrator(authentication));
        return "redirect:/notes?updated";
    }

    @PostMapping("/notes/{id}/delete")
    String deleteNote(@PathVariable Long id, Authentication authentication) {
        herbNoteService.delete(id, authentication.getName(), isAdministrator(authentication));
        return "redirect:/notes?deleted";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/herbs")
    String manageHerbs(Model model) {
        model.addAttribute("herbs", catalogueService.findHerbs(null, null));
        return "admin-herbs";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/herbs/new")
    String newHerb(Model model) {
        model.addAttribute("herbForm", new HerbForm());
        addCategories(model);
        model.addAttribute("formTitle", "Add herb");
        model.addAttribute("formAction", "/admin/herbs");
        return "herb-form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/herbs")
    String createHerb(@Valid @ModelAttribute("herbForm") HerbForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return herbFormWithErrors(model, "Add herb", "/admin/herbs");
        }
        try {
            catalogueService.createHerb(form);
        } catch (ResourceConflictException exception) {
            bindingResult.reject("herb", exception.getMessage());
            return herbFormWithErrors(model, "Add herb", "/admin/herbs");
        }
        return "redirect:/admin/herbs?created";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/herbs/{id}/edit")
    String editHerb(@PathVariable Long id, Model model) {
        model.addAttribute("herbForm", herbForm(catalogueService.findHerbById(id)));
        addCategories(model);
        model.addAttribute("formTitle", "Edit herb");
        model.addAttribute("formAction", "/admin/herbs/" + id);
        return "herb-form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/herbs/{id}")
    String updateHerb(@PathVariable Long id, @Valid @ModelAttribute("herbForm") HerbForm form,
                      BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return herbFormWithErrors(model, "Edit herb", "/admin/herbs/" + id);
        }
        try {
            catalogueService.updateHerb(id, form);
        } catch (ResourceConflictException exception) {
            bindingResult.reject("herb", exception.getMessage());
            return herbFormWithErrors(model, "Edit herb", "/admin/herbs/" + id);
        }
        return "redirect:/admin/herbs?updated";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/herbs/{id}/delete")
    String deleteHerb(@PathVariable Long id) {
        catalogueService.deleteHerb(id);
        return "redirect:/admin/herbs?deleted";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/categories")
    String manageCategories(Model model) {
        model.addAttribute("categories", catalogueService.findCategories());
        return "admin-categories";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/categories/new")
    String newCategory(Model model) {
        model.addAttribute("categoryForm", new CategoryForm());
        model.addAttribute("formTitle", "Add category");
        model.addAttribute("formAction", "/admin/categories");
        return "category-form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/categories")
    String createCategory(@Valid @ModelAttribute("categoryForm") CategoryForm form, BindingResult bindingResult,
                          Model model) {
        if (bindingResult.hasErrors()) {
            return categoryFormWithErrors(model, "Add category", "/admin/categories");
        }
        try {
            catalogueService.createCategory(form);
        } catch (ResourceConflictException exception) {
            bindingResult.reject("category", exception.getMessage());
            return categoryFormWithErrors(model, "Add category", "/admin/categories");
        }
        return "redirect:/admin/categories?created";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/categories/{id}/edit")
    String editCategory(@PathVariable Long id, Model model) {
        model.addAttribute("categoryForm", categoryForm(catalogueService.findCategoryById(id)));
        model.addAttribute("formTitle", "Edit category");
        model.addAttribute("formAction", "/admin/categories/" + id);
        return "category-form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/categories/{id}")
    String updateCategory(@PathVariable Long id, @Valid @ModelAttribute("categoryForm") CategoryForm form,
                          BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return categoryFormWithErrors(model, "Edit category", "/admin/categories/" + id);
        }
        try {
            catalogueService.updateCategory(id, form);
        } catch (ResourceConflictException exception) {
            bindingResult.reject("category", exception.getMessage());
            return categoryFormWithErrors(model, "Edit category", "/admin/categories/" + id);
        }
        return "redirect:/admin/categories?updated";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/categories/{id}/delete")
    String deleteCategory(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            catalogueService.deleteCategory(id);
            return "redirect:/admin/categories?deleted";
        } catch (ResourceConflictException exception) {
            attributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/admin/categories";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    String manageUsers(Model model) {
        model.addAttribute("users", userAccountService.findAll());
        return "admin-users";
    }

    private String herbFormWithErrors(Model model, String title, String action) {
        addCategories(model);
        model.addAttribute("formTitle", title);
        model.addAttribute("formAction", action);
        return "herb-form";
    }

    private String categoryFormWithErrors(Model model, String title, String action) {
        model.addAttribute("formTitle", title);
        model.addAttribute("formAction", action);
        return "category-form";
    }

    private void addCategories(Model model) {
        model.addAttribute("categories", catalogueService.findCategories());
    }

    private void addHerbs(Model model) {
        model.addAttribute("herbs", catalogueService.findHerbs(null, null));
    }

    private HerbForm herbForm(HerbResponse herb) {
        HerbForm form = new HerbForm();
        form.setName(herb.name());
        form.setBotanicalName(herb.botanicalName());
        form.setDescription(herb.description());
        form.setCategoryId(herb.categoryId());
        return form;
    }

    private CategoryForm categoryForm(CategoryResponse category) {
        CategoryForm form = new CategoryForm();
        form.setName(category.name());
        form.setDescription(category.description());
        return form;
    }

    private HerbNoteForm noteForm(HerbNoteResponse note) {
        HerbNoteForm form = new HerbNoteForm();
        form.setHerbId(note.herbId());
        form.setTitle(note.title());
        form.setBody(note.body());
        return form;
    }

    private boolean isAdministrator(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
