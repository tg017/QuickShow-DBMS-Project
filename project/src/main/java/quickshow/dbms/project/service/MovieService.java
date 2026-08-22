package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;
import quickshow.dbms.project.exception.ResourceNotFoundException;
import quickshow.dbms.project.model.Movie;
import quickshow.dbms.project.model.MovieCast;
import quickshow.dbms.project.repository.MovieRepository;
import quickshow.dbms.project.repository.MovieCastRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MovieService {

        private final MovieRepository movieRepository;
        private final MovieCastRepository movieCastRepository;

        public MovieService(
                        MovieRepository movieRepository,
                        MovieCastRepository movieCastRepository) {
                this.movieRepository = movieRepository;
                this.movieCastRepository = movieCastRepository;
        }

        // =========================================================
        // CREATE
        // =========================================================

        public Movie createMovie(Movie movie) {

                if (movie == null) {
                        throw new IllegalArgumentException(
                                        "Movie cannot be null");
                }

                return movieRepository.create(movie);
        }

        // =========================================================
        // READ BY ID
        // =========================================================

        public Movie getMovieById(Integer movieId) {

                if (movieId == null) {
                        throw new IllegalArgumentException(
                                        "Movie ID cannot be null");
                }

                Movie movie = movieRepository.findById(movieId);

                if (movie == null) {
                        throw new ResourceNotFoundException(
                                        "Movie not found with ID: " + movieId);
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
                        Movie movie) {

                if (movieId == null) {
                        throw new IllegalArgumentException(
                                        "Movie ID cannot be null");
                }

                if (movie == null) {
                        throw new IllegalArgumentException(
                                        "Movie cannot be null");
                }

                Movie existingMovie = movieRepository.findById(movieId);

                if (existingMovie == null) {
                        throw new ResourceNotFoundException(
                                        "Movie not found with ID: " + movieId);
                }

                movie.setMovieId(movieId);

                int rowsUpdated = movieRepository.update(movie);

                if (rowsUpdated != 1) {
                        throw new IllegalStateException(
                                        "Movie could not be updated");
                }

                return movieRepository.findById(movieId);
        }

        // =========================================================
        // DELETE
        // =========================================================

        public void deleteMovie(Integer movieId) {

                if (movieId == null) {
                        throw new IllegalArgumentException(
                                        "Movie ID cannot be null");
                }

                if (!movieRepository.existsById(movieId)) {
                        throw new ResourceNotFoundException(
                                        "Movie not found with ID: " + movieId);
                }

                int rowsDeleted = movieRepository.deleteById(movieId);

                if (rowsDeleted != 1) {
                        throw new IllegalStateException(
                                        "Movie could not be deleted");
                }
        }

        // =========================================================
        // COMBINED MOVIE SEARCH
        // =========================================================

        public List<Movie> searchMovies(
                        String title,
                        String language,
                        String genre,
                        String certificate,
                        BigDecimal minRating) {

                if (title != null && title.isBlank()) {
                        title = null;
                }

                if (language != null && language.isBlank()) {
                        language = null;
                }

                if (genre != null && genre.isBlank()) {
                        genre = null;
                }

                if (certificate != null && certificate.isBlank()) {
                        certificate = null;
                }

                if (minRating != null && minRating.compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException(
                                        "Minimum rating cannot be negative");
                }

                return movieRepository.searchMovies(
                                title,
                                language,
                                genre,
                                certificate,
                                minRating);
        }

        // =========================================================
        // GET MOVIE CAST
        // =========================================================

        public List<MovieCast> getMovieCast(
                        Integer movieId) {

                if (movieId == null) {
                        throw new IllegalArgumentException(
                                        "Movie ID cannot be null");
                }

                if (!movieRepository.existsById(movieId)) {
                        throw new ResourceNotFoundException(
                                        "Movie not found with ID: " + movieId);
                }

                return movieCastRepository.findByMovie(
                                movieId);
        }
}