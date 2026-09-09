package gympie.springboot.repository;

import gympie.springboot.entity.Herb;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HerbRepository extends JpaRepository<Herb, Long> {

    @Query("select h from Herb h join fetch h.category order by lower(h.name)")
    List<Herb> findAllWithCategory();

    @Query("select h from Herb h join fetch h.category where h.id = :id")
    Optional<Herb> findWithCategoryById(@Param("id") Long id);

    @Query("""
            select h from Herb h join fetch h.category
            where lower(h.name) like lower(concat('%', :query, '%'))
               or lower(h.botanicalName) like lower(concat('%', :query, '%'))
               or lower(h.description) like lower(concat('%', :query, '%'))
            order by lower(h.name)
            """)
    List<Herb> searchByText(@Param("query") String query);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    long countByCategoryId(Long categoryId);
}
