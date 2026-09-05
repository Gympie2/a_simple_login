package gympie.springboot.a_simple_login.service;

import gympie.springboot.a_simple_login.dto.CategoryForm;
import gympie.springboot.a_simple_login.dto.CategoryResponse;
import gympie.springboot.a_simple_login.dto.HerbForm;
import gympie.springboot.a_simple_login.dto.HerbResponse;
import gympie.springboot.a_simple_login.entity.Herb;
import gympie.springboot.a_simple_login.entity.HerbCategory;
import gympie.springboot.a_simple_login.exception.ResourceConflictException;
import gympie.springboot.a_simple_login.exception.ResourceNotFoundException;
import gympie.springboot.a_simple_login.repository.HerbCategoryRepository;
import gympie.springboot.a_simple_login.repository.HerbRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CatalogueService {

    private final HerbRepository herbRepository;
    private final HerbCategoryRepository categoryRepository;

    public CatalogueService(HerbRepository herbRepository, HerbCategoryRepository categoryRepository) {
        this.herbRepository = herbRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<HerbResponse> findHerbs(String query, Long categoryId) {
        List<Herb> herbs = query == null || query.isBlank()
                ? herbRepository.findAllWithCategory()
                : herbRepository.searchByText(query.trim());
        return herbs.stream()
                .filter(herb -> categoryId == null || herb.getCategory().getId().equals(categoryId))
                .map(this::toHerbResponse)
                .toList();
    }

    public HerbResponse findHerbById(Long id) {
        return toHerbResponse(getHerb(id));
    }

    public List<CategoryResponse> findCategories() {
        return categoryRepository.findAllByOrderByNameAsc().stream().map(this::toCategoryResponse).toList();
    }

    public CategoryResponse findCategoryById(Long id) {
        return toCategoryResponse(getCategory(id));
    }

    @Transactional
    public HerbResponse createHerb(HerbForm form) {
        requireAvailableHerbName(form.getName(), null);
        Herb herb = new Herb(clean(form.getName()), clean(form.getBotanicalName()), clean(form.getDescription()),
                getCategory(form.getCategoryId()));
        return toHerbResponse(herbRepository.save(herb));
    }

    @Transactional
    public HerbResponse updateHerb(Long id, HerbForm form) {
        Herb herb = getHerb(id);
        requireAvailableHerbName(form.getName(), id);
        herb.update(clean(form.getName()), clean(form.getBotanicalName()), clean(form.getDescription()),
                getCategory(form.getCategoryId()));
        return toHerbResponse(herbRepository.save(herb));
    }

    @Transactional
    public void deleteHerb(Long id) {
        herbRepository.delete(getHerb(id));
    }

    @Transactional
    public CategoryResponse createCategory(CategoryForm form) {
        requireAvailableCategoryName(form.getName(), null);
        HerbCategory category = new HerbCategory(clean(form.getName()), clean(form.getDescription()));
        return toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryForm form) {
        HerbCategory category = getCategory(id);
        requireAvailableCategoryName(form.getName(), id);
        category.update(clean(form.getName()), clean(form.getDescription()));
        return toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (herbRepository.countByCategoryId(id) > 0) {
            throw new ResourceConflictException("Move or delete the herbs in this category before deleting it.");
        }
        categoryRepository.delete(getCategory(id));
    }

    private Herb getHerb(Long id) {
        return herbRepository.findWithCategoryById(id).orElseThrow(() -> new ResourceNotFoundException("Herb", id));
    }

    private HerbCategory getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    private void requireAvailableHerbName(String name, Long id) {
        String cleaned = clean(name);
        boolean exists = id == null ? herbRepository.existsByNameIgnoreCase(cleaned)
                : herbRepository.existsByNameIgnoreCaseAndIdNot(cleaned, id);
        if (exists) {
            throw new ResourceConflictException("An herb with that name already exists.");
        }
    }

    private void requireAvailableCategoryName(String name, Long id) {
        String cleaned = clean(name);
        boolean exists = id == null ? categoryRepository.existsByNameIgnoreCase(cleaned)
                : categoryRepository.existsByNameIgnoreCaseAndIdNot(cleaned, id);
        if (exists) {
            throw new ResourceConflictException("A category with that name already exists.");
        }
    }

    private CategoryResponse toCategoryResponse(HerbCategory category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }

    private HerbResponse toHerbResponse(Herb herb) {
        return new HerbResponse(herb.getId(), herb.getName(), herb.getBotanicalName(), herb.getDescription(),
                herb.getCategory().getId(), herb.getCategory().getName());
    }

    private String clean(String value) {
        return value.trim();
    }
}
