package quickshow.dbms.project.repository;

import quickshow.dbms.project.model.MovieCast;
import quickshow.dbms.project.model.MovieCastId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieCastRepository
        extends JpaRepository<MovieCast, MovieCastId> {
}