package gympie.springboot.a_simple_login.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gympie.springboot.a_simple_login.entity.Herb;
import gympie.springboot.a_simple_login.entity.HerbCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class HerbRepositoryTests {

    @Autowired
    private HerbRepository herbRepository;
    @Autowired
    private HerbCategoryRepository categoryRepository;

    @Test
    void customSearchFindsAHerbByItsDescription() {
        HerbCategory category = categoryRepository.save(new HerbCategory("Test category", "Used by this test"));
        herbRepository.saveAndFlush(new Herb("Calendula", "Calendula officinalis",
                "A bright flower recorded in many garden notebooks.", category));

        assertThat(herbRepository.searchByText("garden")).extracting(Herb::getName).contains("Calendula");
    }
}
