package quickshow.dbms.project.repository;

import quickshow.dbms.project.model.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreenRepository extends JpaRepository<Screen, Integer> {
}