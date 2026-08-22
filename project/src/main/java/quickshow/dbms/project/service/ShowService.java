package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;
import quickshow.dbms.project.exception.ResourceNotFoundException;
import quickshow.dbms.project.model.Show;
import quickshow.dbms.project.repository.ShowRepository;

import java.util.List;

@Service
public class ShowService {

    private final ShowRepository showRepository;

    public ShowService(
            ShowRepository showRepository
    ) {
        this.showRepository = showRepository;
    }

    // =========================================================
    // GET SHOWS FOR MOVIE
    // =========================================================

    public List<Show> getShowsByMovie(
            Integer movieId
    ) {

        if (movieId == null) {
            throw new IllegalArgumentException(
                    "Movie ID cannot be null"
            );
        }

        return showRepository.findByMovie(
                movieId
        );
    }


}