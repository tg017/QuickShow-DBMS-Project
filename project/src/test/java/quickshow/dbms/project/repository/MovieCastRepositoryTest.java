package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickshow.dbms.project.model.Movie;
import quickshow.dbms.project.model.MovieCast;
import quickshow.dbms.project.model.MovieCastId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MovieCastRepositoryTest {

    @Autowired
    private MovieCastRepository movieCastRepository;

    @Autowired
    private MovieRepository movieRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateMovieCast() {

        Movie movie =
                createAndSaveMovie();

        MovieCast movieCast =
                createMovieCast(
                        movie.getMovieId(),
                        "Leonardo DiCaprio"
                );

        MovieCast created =
                movieCastRepository.create(
                        movieCast
                );

        assertNotNull(created);
        assertNotNull(created.getId());

        assertEquals(
                movie.getMovieId(),
                created.getId().getMovieId()
        );

        assertEquals(
                "Leonardo DiCaprio",
                created.getId().getActor()
        );
    }


    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldCreateAndRetrieveMovieCast() {

        Movie movie =
                createAndSaveMovie();

        MovieCast movieCast =
                createMovieCast(
                        movie.getMovieId(),
                        "Tom Hanks"
                );

        movieCastRepository.create(
                movieCast
        );

        MovieCast retrieved =
                movieCastRepository.findById(
                        movieCast.getId()
                );

        assertNotNull(retrieved);

        assertEquals(
                movieCast.getId(),
                retrieved.getId()
        );

        assertEquals(
                movie.getMovieId(),
                retrieved.getId().getMovieId()
        );

        assertEquals(
                "Tom Hanks",
                retrieved.getId().getActor()
        );
    }


    // =========================================================
    // FIND NON-EXISTENT
    // =========================================================

    @Test
    void shouldReturnNullForNonExistentMovieCast() {

        MovieCastId id =
                new MovieCastId(
                        Integer.MAX_VALUE,
                        "Non Existent Actor"
                );

        MovieCast result =
                movieCastRepository.findById(id);

        assertNull(result);
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindAllMovieCastEntries() {

        Movie movie =
                createAndSaveMovie();

        MovieCast cast1 =
                createMovieCast(
                        movie.getMovieId(),
                        "Actor One"
                );

        MovieCast cast2 =
                createMovieCast(
                        movie.getMovieId(),
                        "Actor Two"
                );

        movieCastRepository.create(cast1);
        movieCastRepository.create(cast2);

        List<MovieCast> results =
                movieCastRepository.findAll();

        assertTrue(
                results.stream()
                        .anyMatch(cast ->
                                cast.getId().equals(
                                        cast1.getId()
                                )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(cast ->
                                cast.getId().equals(
                                        cast2.getId()
                                )
                        )
        );
    }


    // =========================================================
    // FIND BY MOVIE
    // =========================================================

    @Test
    void shouldFindAllActorsForMovie() {

        Movie movie1 =
                createAndSaveMovie();

        Movie movie2 =
                createAndSaveMovie();

        MovieCast cast1 =
                createMovieCast(
                        movie1.getMovieId(),
                        "Actor One"
                );

        MovieCast cast2 =
                createMovieCast(
                        movie1.getMovieId(),
                        "Actor Two"
                );

        MovieCast cast3 =
                createMovieCast(
                        movie2.getMovieId(),
                        "Actor Three"
                );

        movieCastRepository.create(cast1);
        movieCastRepository.create(cast2);
        movieCastRepository.create(cast3);

        List<MovieCast> results =
                movieCastRepository.findByMovie(
                        movie1.getMovieId()
                );

        assertTrue(
                results.stream()
                        .anyMatch(cast ->
                                cast.getId().equals(
                                        cast1.getId()
                                )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(cast ->
                                cast.getId().equals(
                                        cast2.getId()
                                )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(cast ->
                                cast.getId().equals(
                                        cast3.getId()
                                )
                        )
        );
    }


    // =========================================================
    // FIND BY ACTOR
    // =========================================================

    @Test
    void shouldFindMoviesForActor() {

        Movie movie1 =
                createAndSaveMovie();

        Movie movie2 =
                createAndSaveMovie();

        Movie movie3 =
                createAndSaveMovie();

        MovieCast cast1 =
                createMovieCast(
                        movie1.getMovieId(),
                        "Tom Hanks"
                );

        MovieCast cast2 =
                createMovieCast(
                        movie2.getMovieId(),
                        "Tom Hanks"
                );

        MovieCast cast3 =
                createMovieCast(
                        movie3.getMovieId(),
                        "Brad Pitt"
                );

        movieCastRepository.create(cast1);
        movieCastRepository.create(cast2);
        movieCastRepository.create(cast3);

        List<MovieCast> results =
                movieCastRepository.findByActor(
                        "Tom Hanks"
                );

        assertTrue(
                results.stream()
                        .anyMatch(cast ->
                                cast.getId().equals(
                                        cast1.getId()
                                )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(cast ->
                                cast.getId().equals(
                                        cast2.getId()
                                )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(cast ->
                                cast.getId().equals(
                                        cast3.getId()
                                )
                        )
        );
    }


    // =========================================================
    // EXISTS
    // =========================================================

    @Test
    void shouldCheckMovieCastExistence() {

        Movie movie =
                createAndSaveMovie();

        MovieCast movieCast =
                createMovieCast(
                        movie.getMovieId(),
                        "Actor"
                );

        movieCastRepository.create(
                movieCast
        );

        assertTrue(
                movieCastRepository.existsById(
                        movieCast.getId()
                )
        );

        assertFalse(
                movieCastRepository.existsById(
                        new MovieCastId(
                                Integer.MAX_VALUE,
                                "Unknown"
                        )
                )
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteMovieCast() {

        Movie movie =
                createAndSaveMovie();

        MovieCast movieCast =
                createMovieCast(
                        movie.getMovieId(),
                        "Delete Actor"
                );

        movieCastRepository.create(
                movieCast
        );

        assertTrue(
                movieCastRepository.existsById(
                        movieCast.getId()
                )
        );

        int rowsDeleted =
                movieCastRepository.deleteById(
                        movieCast.getId()
                );

        assertEquals(
                1,
                rowsDeleted
        );

        assertFalse(
                movieCastRepository.existsById(
                        movieCast.getId()
                )
        );

        assertNull(
                movieCastRepository.findById(
                        movieCast.getId()
                )
        );
    }


    // =========================================================
    // DELETE NON-EXISTENT
    // =========================================================

    @Test
    void shouldDeleteZeroRowsForNonExistentMovieCast() {

        MovieCastId id =
                new MovieCastId(
                        Integer.MAX_VALUE,
                        "Unknown"
                );

        int rowsDeleted =
                movieCastRepository.deleteById(id);

        assertEquals(
                0,
                rowsDeleted
        );
    }


    // =========================================================
    // DUPLICATE COMPOSITE KEY
    // =========================================================

    @Test
    void shouldRejectDuplicateMovieCast() {

        Movie movie =
                createAndSaveMovie();

        MovieCast cast1 =
                createMovieCast(
                        movie.getMovieId(),
                        "Same Actor"
                );

        movieCastRepository.create(cast1);

        MovieCast cast2 =
                createMovieCast(
                        movie.getMovieId(),
                        "Same Actor"
                );

        assertThrows(
                Exception.class,
                () -> movieCastRepository.create(cast2)
        );
    }


    // =========================================================
    // SAME ACTOR FOR DIFFERENT MOVIES
    // =========================================================

    @Test
    void shouldAllowSameActorForDifferentMovies() {

        Movie movie1 =
                createAndSaveMovie();

        Movie movie2 =
                createAndSaveMovie();

        MovieCast cast1 =
                createMovieCast(
                        movie1.getMovieId(),
                        "Same Actor"
                );

        MovieCast cast2 =
                createMovieCast(
                        movie2.getMovieId(),
                        "Same Actor"
                );

        assertDoesNotThrow(
                () -> movieCastRepository.create(cast1)
        );

        assertDoesNotThrow(
                () -> movieCastRepository.create(cast2)
        );
    }


    // =========================================================
    // DIFFERENT ACTORS FOR SAME MOVIE
    // =========================================================

    @Test
    void shouldAllowDifferentActorsForSameMovie() {

        Movie movie =
                createAndSaveMovie();

        MovieCast cast1 =
                createMovieCast(
                        movie.getMovieId(),
                        "Actor One"
                );

        MovieCast cast2 =
                createMovieCast(
                        movie.getMovieId(),
                        "Actor Two"
                );

        assertDoesNotThrow(
                () -> movieCastRepository.create(cast1)
        );

        assertDoesNotThrow(
                () -> movieCastRepository.create(cast2)
        );
    }


    // =========================================================
    // INVALID MOVIE FOREIGN KEY
    // =========================================================

    @Test
    void shouldRejectNonExistentMovie() {

        MovieCast cast =
                createMovieCast(
                        Integer.MAX_VALUE,
                        "Actor"
                );

        assertThrows(
                Exception.class,
                () -> movieCastRepository.create(cast)
        );
    }


    // =========================================================
    // NULL ACTOR
    // =========================================================

    @Test
    void shouldRejectNullActor() {

        Movie movie =
                createAndSaveMovie();

        MovieCast cast =
                createMovieCast(
                        movie.getMovieId(),
                        null
                );

        assertThrows(
                Exception.class,
                () -> movieCastRepository.create(cast)
        );
    }


    // =========================================================
    // ACTOR LENGTH = 100
    // =========================================================

    @Test
    void shouldAllowActorOfMaximumLength() {

        Movie movie =
                createAndSaveMovie();

        String actor =
                "A".repeat(100);

        MovieCast cast =
                createMovieCast(
                        movie.getMovieId(),
                        actor
                );

        assertDoesNotThrow(
                () -> movieCastRepository.create(cast)
        );
    }


    // =========================================================
    // ACTOR LENGTH > 100
    // =========================================================

    @Test
    void shouldRejectActorExceedingMaximumLength() {

        Movie movie =
                createAndSaveMovie();

        String actor =
                "A".repeat(101);

        MovieCast cast =
                createMovieCast(
                        movie.getMovieId(),
                        actor
                );

        assertThrows(
                Exception.class,
                () -> movieCastRepository.create(cast)
        );
    }


    // =========================================================
    // CASCADE DELETE
    // =========================================================

    @Test
    void shouldDeleteMovieCastWhenMovieIsDeleted() {

        Movie movie =
                createAndSaveMovie();

        MovieCast cast =
                createMovieCast(
                        movie.getMovieId(),
                        "Cascade Actor"
                );

        movieCastRepository.create(cast);

        assertTrue(
                movieCastRepository.existsById(
                        cast.getId()
                )
        );

        movieRepository.deleteById(
                movie.getMovieId()
        );

        assertFalse(
                movieCastRepository.existsById(
                        cast.getId()
                )
        );
    }


    // =========================================================
    // HELPER
    // =========================================================

    private MovieCast createMovieCast(
            Integer movieId,
            String actor
    ) {

        MovieCast movieCast =
                new MovieCast();

        movieCast.setId(
                new MovieCastId(
                        movieId,
                        actor
                )
        );

        return movieCast;
    }


    private Movie createAndSaveMovie() {

        Movie movie =
                new Movie();

        movie.setTitle(
                "MovieCast Test Movie"
        );

        movie.setLanguage(
                "English"
        );

        movie.setGenre(
                "Drama"
        );

        movie.setDuration(120);

        movie.setReleaseDate(
                LocalDate.of(2026, 1, 1)
        );

        movie.setImdbRating(
                new BigDecimal("8.5")
        );

        movie.setDirector(
                "Test Director"
        );

        movie.setDescription(
                "Movie used for MovieCast repository tests."
        );

        return movieRepository.create(movie);
    }
}