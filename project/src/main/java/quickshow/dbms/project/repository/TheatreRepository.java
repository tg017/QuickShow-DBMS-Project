package quickshow.dbms.project.repository;

import quickshow.dbms.project.model.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheatreRepository extends JpaRepository<Theatre, Integer> {
}
