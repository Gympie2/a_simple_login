package gympie.springboot.repository;

import gympie.springboot.entity.HerbCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HerbCategoryRepository extends JpaRepository<HerbCategory, Long> {

    List<HerbCategory> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
