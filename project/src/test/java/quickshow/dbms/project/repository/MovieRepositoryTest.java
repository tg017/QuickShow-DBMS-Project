package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import quickshow.dbms.project.model.Movie;
import quickshow.dbms.project.model.Certificate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void shouldSaveAndRetrieveMovie() {

        Movie movie = new Movie();

        movie.setTitle("Interstellar");
        movie.setLanguage("English");
        movie.setGenre("Sci-Fi");
        movie.setDuration(169);
        movie.setReleaseDate(LocalDate.of(2014, 11, 7));
        movie.setImdbRating(new BigDecimal("8.7"));
        movie.setCertificate(Certificate.UA_13_PLUS);
        movie.setDirector("Christopher Nolan");
        movie.setDescription("A science-fiction film about space and time.");

        Movie savedMovie = movieRepository.save(movie);

        assertNotNull(savedMovie.getMovieId());

        Movie retrievedMovie =
                movieRepository.findById(savedMovie.getMovieId())
                        .orElseThrow();

        assertEquals("Interstellar", retrievedMovie.getTitle());
        assertEquals("English", retrievedMovie.getLanguage());
        assertEquals(169, retrievedMovie.getDuration());
        assertEquals(
                new BigDecimal("8.7"),
                retrievedMovie.getImdbRating()
        );
    }
}
