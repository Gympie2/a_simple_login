package gympie.springboot.a_simple_login.repository;

import gympie.springboot.a_simple_login.entity.HerbNote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HerbNoteRepository extends JpaRepository<HerbNote, Long> {

    List<HerbNote> findByOwnerUsernameOrderByCreatedAtDesc(String username);

    List<HerbNote> findAllByOrderByCreatedAtDesc();
}
