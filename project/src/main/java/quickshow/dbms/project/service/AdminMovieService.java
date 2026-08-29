package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;
import quickshow.dbms.project.dto.MovieDetailsDTO;
import quickshow.dbms.project.dto.MovieListDTO;
import quickshow.dbms.project.model.Movie;
import quickshow.dbms.project.model.MovieCast;
import quickshow.dbms.project.model.MovieCastId;
import quickshow.dbms.project.repository.AdminMovieRepository;

import java.util.List;

@Service
public class AdminMovieService {

    private final AdminMovieRepository adminMovieRepository;

    public AdminMovieService(
            AdminMovieRepository adminMovieRepository
    ) {
        this.adminMovieRepository = adminMovieRepository;
    }

    public List<MovieListDTO> getAllMovies() {

        return adminMovieRepository.findAllMovies();
    }

    public MovieDetailsDTO getMovieById(Integer movieId) {

        MovieDetailsDTO movie =
                adminMovieRepository.findMovieDetailsById(movieId);

        if (movie == null) {
            return null;
        }

        return movie;
    }

    public Movie createMovie(Movie movie) {

        if (movie == null) {
            throw new IllegalArgumentException(
                    "Movie cannot be null"
            );
        }

        Integer movieId =
                adminMovieRepository.createMovie(movie);

        movie.setMovieId(movieId);

        return movie;
    }

    public Movie updateMovie(
            Integer movieId,
            Movie movie
    ) {

        if (!adminMovieRepository.existsById(movieId)) {
            return null;
        }

        movie.setMovieId(movieId);

        adminMovieRepository.updateMovie(movie);

        return movie;
    }

    public String deleteMovie(Integer movieId) {

        if (!adminMovieRepository.existsById(movieId)) {
            return "NOT_FOUND";
        }

        if (adminMovieRepository.hasShows(movieId)) {
            return "HAS_SHOWS";
        }

        if (adminMovieRepository.hasCast(movieId)) {
            adminMovieRepository.deleteMovieCast(movieId);
        }

        int deleted =
                adminMovieRepository.deleteMovie(movieId);

        if (deleted == 0) {
            return "NOT_FOUND";
        }

        return "DELETED";
    }
}