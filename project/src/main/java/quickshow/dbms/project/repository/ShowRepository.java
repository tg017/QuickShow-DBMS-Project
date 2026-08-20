package quickshow.dbms.project.repository;

import quickshow.dbms.project.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Integer> {
}