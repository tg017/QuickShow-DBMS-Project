package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;
import quickshow.dbms.project.model.Movie;
import quickshow.dbms.project.repository.MovieRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }


    // =========================================================
    // CREATE
    // =========================================================

    public Movie createMovie(Movie movie) {

        if (movie == null) {
            throw new IllegalArgumentException(
                    "Movie cannot be null"
            );
        }

        return movieRepository.create(movie);
    }


    // =========================================================
    // READ BY ID
    // =========================================================

    public Movie getMovieById(Integer movieId) {

        if (movieId == null) {
            throw new IllegalArgumentException(
                    "Movie ID cannot be null"
            );
        }

        Movie movie =
                movieRepository.findById(movieId);

        if (movie == null) {
            throw new IllegalArgumentException(
                    "Movie not found with ID: " + movieId
            );
        }

        return movie;
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<Movie> getAllMovies() {

        return movieRepository.findAll();
    }


    // =========================================================
    // UPDATE
    // =========================================================

    public Movie updateMovie(
            Integer movieId,
            Movie movie
    ) {

        if (movieId == null) {
            throw new IllegalArgumentException(
                    "Movie ID cannot be null"
            );
        }

        if (movie == null) {
            throw new IllegalArgumentException(
                    "Movie cannot be null"
            );
        }

        Movie existingMovie =
                movieRepository.findById(movieId);

        if (existingMovie == null) {
            throw new IllegalArgumentException(
                    "Movie not found with ID: " + movieId
            );
        }

        movie.setMovieId(movieId);

        int rowsUpdated =
                movieRepository.update(movie);

        if (rowsUpdated != 1) {
            throw new IllegalStateException(
                    "Movie could not be updated"
            );
        }

        return movieRepository.findById(movieId);
    }


    // =========================================================
    // DELETE
    // =========================================================

    public void deleteMovie(
            Integer movieId
    ) {

        if (movieId == null) {
            throw new IllegalArgumentException(
                    "Movie ID cannot be null"
            );
        }

        if (!movieRepository.existsById(movieId)) {
            throw new IllegalArgumentException(
                    "Movie not found with ID: " + movieId
            );
        }

        int rowsDeleted =
                movieRepository.deleteById(movieId);

        if (rowsDeleted != 1) {
            throw new IllegalStateException(
                    "Movie could not be deleted"
            );
        }
    }


    // =========================================================
    // SEARCH BY LANGUAGE
    // =========================================================

    public List<Movie> getMoviesByLanguage(
            String language
    ) {

        if (language == null || language.isBlank()) {
            throw new IllegalArgumentException(
                    "Language cannot be empty"
            );
        }

        return movieRepository.findByLanguage(
                language
        );
    }


    // =========================================================
    // SEARCH BY GENRE + MINIMUM RATING
    // =========================================================

    public List<Movie> getMoviesByGenreAndMinimumRating(
            String genre,
            BigDecimal minimumRating
    ) {

        if (genre == null || genre.isBlank()) {
            throw new IllegalArgumentException(
                    "Genre cannot be empty"
            );
        }

        if (minimumRating == null) {
            throw new IllegalArgumentException(
                    "Minimum rating cannot be null"
            );
        }

        return movieRepository
                .findByGenreAndMinimumRating(
                        genre,
                        minimumRating
                );
    }
}