package gympie.springboot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gympie.springboot.dto.HerbForm;
import gympie.springboot.entity.Herb;
import gympie.springboot.entity.HerbCategory;
import gympie.springboot.exception.ResourceConflictException;
import gympie.springboot.repository.HerbCategoryRepository;
import gympie.springboot.repository.HerbRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatalogueServiceTests {

    @Mock
    private HerbRepository herbRepository;
    @Mock
    private HerbCategoryRepository categoryRepository;
    @InjectMocks
    private CatalogueService catalogueService;

    @Test
    void createsHerbInTheSelectedCategory() {
        HerbCategory category = new HerbCategory("Culinary herbs", "Kitchen plants");
        when(categoryRepository.findById(8L)).thenReturn(Optional.of(category));
        when(herbRepository.save(any(Herb.class))).thenAnswer(invocation -> invocation.getArgument(0));

        catalogueService.createHerb(form());

        ArgumentCaptor<Herb> saved = ArgumentCaptor.forClass(Herb.class);
        verify(herbRepository).save(saved.capture());
        assertThat(saved.getValue().getName()).isEqualTo("Mint");
        assertThat(saved.getValue().getBotanicalName()).isEqualTo("Mentha spicata");
        assertThat(saved.getValue().getCategory()).isSameAs(category);
    }

    @Test
    void rejectsDuplicateHerbNames() {
        when(herbRepository.existsByNameIgnoreCase("Mint")).thenReturn(true);

        assertThatThrownBy(() -> catalogueService.createHerb(form()))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("already exists");
    }

    private HerbForm form() {
        HerbForm form = new HerbForm();
        form.setName("Mint");
        form.setBotanicalName("Mentha spicata");
        form.setDescription("Fresh leaf for drinks and teas.");
        form.setCategoryId(8L);
        return form;
    }
}
