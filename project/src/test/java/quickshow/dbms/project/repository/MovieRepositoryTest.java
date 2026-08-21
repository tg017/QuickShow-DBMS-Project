package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickshow.dbms.project.model.Certificate;
import quickshow.dbms.project.model.Movie;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateMovie() {

        Movie movie = createCompleteMovie();

        Movie createdMovie =
                movieRepository.create(movie);

        assertNotNull(createdMovie);
        assertNotNull(createdMovie.getMovieId());

        assertEquals(
                "Interstellar",
                createdMovie.getTitle()
        );

        assertEquals(
                "English",
                createdMovie.getLanguage()
        );

        assertEquals(
                Certificate.UA_13_PLUS,
                createdMovie.getCertificate()
        );
    }


    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldCreateAndRetrieveMovieById() {

        Movie movie = createCompleteMovie();

        Movie createdMovie =
                movieRepository.create(movie);

        Movie retrievedMovie =
                movieRepository.findById(
                        createdMovie.getMovieId()
                );

        assertNotNull(retrievedMovie);

        assertEquals(
                createdMovie.getMovieId(),
                retrievedMovie.getMovieId()
        );

        assertEquals(
                "Interstellar",
                retrievedMovie.getTitle()
        );

        assertEquals(
                "interstellar.jpg",
                retrievedMovie.getPoster()
        );

        assertEquals(
                "English",
                retrievedMovie.getLanguage()
        );

        assertEquals(
                "Sci-Fi",
                retrievedMovie.getGenre()
        );

        assertEquals(
                169,
                retrievedMovie.getDuration()
        );

        assertEquals(
                LocalDate.of(2014, 11, 7),
                retrievedMovie.getReleaseDate()
        );

        assertEquals(
                new BigDecimal("8.7"),
                retrievedMovie.getImdbRating()
        );

        assertEquals(
                Certificate.UA_13_PLUS,
                retrievedMovie.getCertificate()
        );

        assertEquals(
                "Christopher Nolan",
                retrievedMovie.getDirector()
        );

        assertEquals(
                "A science-fiction film about space and time.",
                retrievedMovie.getDescription()
        );
    }


    // =========================================================
    // FIND NON-EXISTENT MOVIE
    // =========================================================

    @Test
    void shouldReturnNullForNonExistentMovie() {

        Movie movie =
                movieRepository.findById(
                        Integer.MAX_VALUE
                );

        assertNull(movie);
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindAllMovies() {

        Movie movie1 =
                createMovie(
                        "Repository Test Movie 1",
                        "English"
                );

        Movie movie2 =
                createMovie(
                        "Repository Test Movie 2",
                        "Hindi"
                );

        Movie savedMovie1 =
                movieRepository.create(movie1);

        Movie savedMovie2 =
                movieRepository.create(movie2);

        List<Movie> movies =
                movieRepository.findAll();

        assertTrue(
                movies.stream()
                        .anyMatch(movie ->
                                movie.getMovieId()
                                        .equals(
                                                savedMovie1.getMovieId()
                                        )
                        )
        );

        assertTrue(
                movies.stream()
                        .anyMatch(movie ->
                                movie.getMovieId()
                                        .equals(
                                                savedMovie2.getMovieId()
                                        )
                        )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateMovie() {

        Movie movie =
                movieRepository.create(
                        createCompleteMovie()
                );

        movie.setTitle("Interstellar Updated");
        movie.setGenre("Science Fiction");
        movie.setDuration(170);
        movie.setImdbRating(
                new BigDecimal("9.0")
        );
        movie.setCertificate(
                Certificate.UA_16_PLUS
        );

        int rowsUpdated =
                movieRepository.update(movie);

        assertEquals(1, rowsUpdated);

        Movie updatedMovie =
                movieRepository.findById(
                        movie.getMovieId()
                );

        assertNotNull(updatedMovie);

        assertEquals(
                "Interstellar Updated",
                updatedMovie.getTitle()
        );

        assertEquals(
                "Science Fiction",
                updatedMovie.getGenre()
        );

        assertEquals(
                170,
                updatedMovie.getDuration()
        );

        assertEquals(
                new BigDecimal("9.0"),
                updatedMovie.getImdbRating()
        );

        assertEquals(
                Certificate.UA_16_PLUS,
                updatedMovie.getCertificate()
        );
    }


    // =========================================================
    // UPDATE NON-EXISTENT MOVIE
    // =========================================================

    @Test
    void shouldUpdateZeroRowsForNonExistentMovie() {

        Movie movie =
                createCompleteMovie();

        movie.setMovieId(Integer.MAX_VALUE);

        int rowsUpdated =
                movieRepository.update(movie);

        assertEquals(0, rowsUpdated);
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteMovie() {

        Movie movie =
                movieRepository.create(
                        createCompleteMovie()
                );

        Integer movieId =
                movie.getMovieId();

        assertTrue(
                movieRepository.existsById(movieId)
        );

        int rowsDeleted =
                movieRepository.deleteById(movieId);

        assertEquals(1, rowsDeleted);

        assertFalse(
                movieRepository.existsById(movieId)
        );

        assertNull(
                movieRepository.findById(movieId)
        );
    }


    // =========================================================
    // DELETE NON-EXISTENT MOVIE
    // =========================================================

    @Test
    void shouldDeleteZeroRowsForNonExistentMovie() {

        int rowsDeleted =
                movieRepository.deleteById(
                        Integer.MAX_VALUE
                );

        assertEquals(0, rowsDeleted);
    }


    // =========================================================
    // EXISTS
    // =========================================================

    @Test
    void shouldCheckMovieExistence() {

        Movie movie =
                movieRepository.create(
                        createCompleteMovie()
                );

        Integer movieId =
                movie.getMovieId();

        assertTrue(
                movieRepository.existsById(movieId)
        );

        assertFalse(
                movieRepository.existsById(
                        Integer.MAX_VALUE
                )
        );
    }


    // =========================================================
    // NULLABLE FIELDS
    // =========================================================

    @Test
    void shouldCreateMovieWithNullableFields() {

        Movie movie = new Movie();

        movie.setTitle("Minimal Movie");
        movie.setLanguage("English");
        movie.setDuration(100);
        movie.setReleaseDate(
                LocalDate.of(2026, 1, 1)
        );

        Movie createdMovie =
                movieRepository.create(movie);

        Movie retrievedMovie =
                movieRepository.findById(
                        createdMovie.getMovieId()
                );

        assertNotNull(retrievedMovie);

        assertEquals(
                "Minimal Movie",
                retrievedMovie.getTitle()
        );

        assertNull(retrievedMovie.getPoster());
        assertNull(retrievedMovie.getGenre());
        assertNull(retrievedMovie.getImdbRating());
        assertNull(retrievedMovie.getCertificate());
        assertNull(retrievedMovie.getDirector());
        assertNull(retrievedMovie.getDescription());
    }


    // =========================================================
    // CERTIFICATE MAPPING
    // =========================================================

    @Test
    void shouldPersistCertificateCorrectly() {

        for (Certificate certificate :
                Certificate.values()) {

            Movie movie =
                    createCompleteMovie();

            movie.setTitle(
                    "Certificate Test " +
                            certificate.name()
            );

            movie.setCertificate(certificate);

            Movie createdMovie =
                    movieRepository.create(movie);

            Movie retrievedMovie =
                    movieRepository.findById(
                            createdMovie.getMovieId()
                    );

            assertNotNull(retrievedMovie);

            assertEquals(
                    certificate,
                    retrievedMovie.getCertificate()
            );
        }
    }


    // =========================================================
    // NULL CERTIFICATE
    // =========================================================

    @Test
    void shouldAllowNullCertificate() {

        Movie movie =
                createCompleteMovie();

        movie.setCertificate(null);

        Movie createdMovie =
                movieRepository.create(movie);

        Movie retrievedMovie =
                movieRepository.findById(
                        createdMovie.getMovieId()
                );

        assertNotNull(retrievedMovie);

        assertNull(
                retrievedMovie.getCertificate()
        );
    }


    // =========================================================
    // DECIMAL MAPPING
    // =========================================================

    @Test
    void shouldPersistIMDbRatingCorrectly() {

        Movie movie =
                createCompleteMovie();

        movie.setImdbRating(
                new BigDecimal("9.9")
        );

        Movie createdMovie =
                movieRepository.create(movie);

        Movie retrievedMovie =
                movieRepository.findById(
                        createdMovie.getMovieId()
                );

        assertEquals(
                new BigDecimal("9.9"),
                retrievedMovie.getImdbRating()
        );
    }


    // =========================================================
    // FIND BY LANGUAGE
    // =========================================================

    @Test
    void shouldFindMoviesByLanguage() {

        Movie englishMovie =
                createMovie(
                        "English Test Movie",
                        "English"
                );

        Movie hindiMovie =
                createMovie(
                        "Hindi Test Movie",
                        "Hindi"
                );

        Movie savedEnglish =
                movieRepository.create(
                        englishMovie
                );

        Movie savedHindi =
                movieRepository.create(
                        hindiMovie
                );

        List<Movie> englishMovies =
                movieRepository.findByLanguage(
                        "English"
                );

        assertTrue(
                englishMovies.stream()
                        .anyMatch(movie ->
                                movie.getMovieId()
                                        .equals(
                                                savedEnglish
                                                        .getMovieId()
                                        )
                        )
        );

        assertFalse(
                englishMovies.stream()
                        .anyMatch(movie ->
                                movie.getMovieId()
                                        .equals(
                                                savedHindi
                                                        .getMovieId()
                                        )
                        )
        );
    }


    // =========================================================
    // FIND BY GENRE + MINIMUM RATING
    // =========================================================

    @Test
    void shouldFindMoviesByGenreAndMinimumRating() {

        Movie highRated =
                createCompleteMovie();

        highRated.setTitle(
                "High Rated Sci-Fi"
        );

        highRated.setImdbRating(
                new BigDecimal("9.0")
        );

        Movie lowRated =
                createCompleteMovie();

        lowRated.setTitle(
                "Low Rated Sci-Fi"
        );

        lowRated.setImdbRating(
                new BigDecimal("6.5")
        );

        Movie differentGenre =
                createCompleteMovie();

        differentGenre.setTitle(
                "Drama Movie"
        );

        differentGenre.setGenre("Drama");

        differentGenre.setImdbRating(
                new BigDecimal("9.5")
        );

        Movie savedHigh =
                movieRepository.create(highRated);

        Movie savedLow =
                movieRepository.create(lowRated);

        Movie savedDrama =
                movieRepository.create(differentGenre);

        List<Movie> results =
                movieRepository.findByGenreAndMinimumRating(
                        "Sci-Fi",
                        new BigDecimal("8.0")
                );

        assertTrue(
                results.stream()
                        .anyMatch(movie ->
                                movie.getMovieId()
                                        .equals(
                                                savedHigh
                                                        .getMovieId()
                                        )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(movie ->
                                movie.getMovieId()
                                        .equals(
                                                savedLow
                                                        .getMovieId()
                                        )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(movie ->
                                movie.getMovieId()
                                        .equals(
                                                savedDrama
                                                        .getMovieId()
                                        )
                        )
        );
    }


    // =========================================================
    // ORDERING
    // =========================================================

    @Test
    void shouldReturnMoviesInDescendingRatingOrder() {

        Movie movie1 =
                createCompleteMovie();

        movie1.setTitle("Rating 8");
        movie1.setImdbRating(
                new BigDecimal("8.0")
        );

        Movie movie2 =
                createCompleteMovie();

        movie2.setTitle("Rating 9");
        movie2.setImdbRating(
                new BigDecimal("9.0")
        );

        Movie movie3 =
                createCompleteMovie();

        movie3.setTitle("Rating 8.5");
        movie3.setImdbRating(
                new BigDecimal("8.5")
        );

        movieRepository.create(movie1);
        movieRepository.create(movie2);
        movieRepository.create(movie3);

        List<Movie> results =
                movieRepository.findByGenreAndMinimumRating(
                        "Sci-Fi",
                        new BigDecimal("7.0")
                );

        assertTrue(results.size() >= 3);

        for (int i = 1; i < results.size(); i++) {

            BigDecimal previous =
                    results.get(i - 1)
                            .getImdbRating();

            BigDecimal current =
                    results.get(i)
                            .getImdbRating();

            assertTrue(
                    previous.compareTo(current) >= 0
            );
        }
    }


    // =========================================================
    // NOT NULL CONSTRAINTS
    // =========================================================

    @Test
    void shouldRejectMovieWithoutTitle() {

        Movie movie =
                createCompleteMovie();

        movie.setTitle(null);

        assertThrows(
                Exception.class,
                () -> movieRepository.create(movie)
        );
    }


    @Test
    void shouldRejectMovieWithoutLanguage() {

        Movie movie =
                createCompleteMovie();

        movie.setLanguage(null);

        assertThrows(
                Exception.class,
                () -> movieRepository.create(movie)
        );
    }


    @Test
    void shouldRejectMovieWithoutDuration() {

        Movie movie =
                createCompleteMovie();

        movie.setDuration(null);

        assertThrows(
                Exception.class,
                () -> movieRepository.create(movie)
        );
    }


    @Test
    void shouldRejectMovieWithoutReleaseDate() {

        Movie movie =
                createCompleteMovie();

        movie.setReleaseDate(null);

        assertThrows(
                Exception.class,
                () -> movieRepository.create(movie)
        );
    }


    // =========================================================
    // HELPERS
    // =========================================================

    private Movie createCompleteMovie() {

        Movie movie = new Movie();

        movie.setTitle("Interstellar");
        movie.setPoster("interstellar.jpg");
        movie.setLanguage("English");
        movie.setGenre("Sci-Fi");
        movie.setDuration(169);
        movie.setReleaseDate(
                LocalDate.of(2014, 11, 7)
        );
        movie.setImdbRating(
                new BigDecimal("8.7")
        );
        movie.setCertificate(
                Certificate.UA_13_PLUS
        );
        movie.setDirector(
                "Christopher Nolan"
        );
        movie.setDescription(
                "A science-fiction film about space and time."
        );

        return movie;
    }


    private Movie createMovie(
            String title,
            String language
    ) {

        Movie movie =
                createCompleteMovie();

        movie.setTitle(title);
        movie.setLanguage(language);

        return movie;
    }
}