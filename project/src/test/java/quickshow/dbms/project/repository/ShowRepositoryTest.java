package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import quickshow.dbms.project.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShowRepositoryTest {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Test
    void shouldSaveAndRetrieveShow() {

        // Create Theatre
        Theatre theatre = new Theatre();

        theatre.setName("PVR Phoenix");
        theatre.setContactNo("9876543210");
        theatre.setBuildingName("Phoenix Mall");
        theatre.setStreet("High Street");
        theatre.setArea("Lower Parel");
        theatre.setCity("Mumbai");
        theatre.setState("Maharashtra");
        theatre.setPinCode("400013");

        Theatre savedTheatre = theatreRepository.save(theatre);

        assertNotNull(savedTheatre.getTheatreId());


        // Create Screen
        Screen screen = new Screen();

        screen.setName("Screen 1");
        screen.setScreenType(ScreenType.TWO_D);
        screen.setCapacity(200);
        screen.setTheatre(savedTheatre);

        Screen savedScreen = screenRepository.save(screen);

        assertNotNull(savedScreen.getScreenId());


        // Create Movie
        Movie movie = new Movie();

        movie.setTitle("Interstellar");
        movie.setLanguage("English");
        movie.setGenre("Sci-Fi");
        movie.setDuration(169);
        movie.setReleaseDate(LocalDate.of(2014, 11, 7));
        movie.setImdbRating(new BigDecimal("8.7"));
        movie.setCertificate(Certificate.UA_13_PLUS);
        movie.setDirector("Christopher Nolan");
        movie.setDescription(
                "A science-fiction film about space and time."
        );

        Movie savedMovie = movieRepository.save(movie);

        assertNotNull(savedMovie.getMovieId());


        // Create Show
        Show show = new Show();

        show.setShowDate(LocalDate.of(2026, 9, 1));
        show.setShowTime(LocalTime.of(18, 30));
        show.setTicketPrice(25000L);
        show.setAvailableSeats(200);
        show.setShowStatus(ShowStatus.SCHEDULED);
        show.setMovie(savedMovie);
        show.setScreen(savedScreen);

        Show savedShow = showRepository.save(show);

        assertNotNull(savedShow.getShowId());


        // Retrieve Show
        Show retrievedShow =
                showRepository.findById(savedShow.getShowId())
                        .orElseThrow();


        // Verify Show fields
        assertEquals(
                LocalDate.of(2026, 9, 1),
                retrievedShow.getShowDate()
        );

        assertEquals(
                LocalTime.of(18, 30),
                retrievedShow.getShowTime()
        );

        assertEquals(25000L, retrievedShow.getTicketPrice());

        assertEquals(200, retrievedShow.getAvailableSeats());

        assertEquals(
                ShowStatus.SCHEDULED,
                retrievedShow.getShowStatus()
        );


        // Verify Movie relationship
        assertEquals(
                savedMovie.getMovieId(),
                retrievedShow.getMovie().getMovieId()
        );


        // Verify Screen relationship
        assertEquals(
                savedScreen.getScreenId(),
                retrievedShow.getScreen().getScreenId()
        );


        // Verify Theatre relationship through Screen
        assertEquals(
                savedTheatre.getTheatreId(),
                retrievedShow.getScreen().getTheatre().getTheatreId()
        );
    }

    @Test
    void shouldFindSavedMovies() {

        Movie movie1 = new Movie();

        movie1.setTitle("Repository Test Movie 1");
        movie1.setLanguage("English");
        movie1.setDuration(120);
        movie1.setReleaseDate(LocalDate.of(2026, 1, 1));

        Movie movie2 = new Movie();

        movie2.setTitle("Repository Test Movie 2");
        movie2.setLanguage("Hindi");
        movie2.setDuration(140);
        movie2.setReleaseDate(LocalDate.of(2026, 1, 2));

        Movie savedMovie1 = movieRepository.save(movie1);
        Movie savedMovie2 = movieRepository.save(movie2);

        var movies = movieRepository.findAll();

        assertEquals(
                2,
                movies.stream()
                        .filter(movie ->
                                movie.getMovieId().equals(savedMovie1.getMovieId())
                                        || movie.getMovieId().equals(savedMovie2.getMovieId())
                        )
                        .count()
        );
    }

    @Test
    void shouldUpdateMovie() {

        Movie movie = new Movie();

        movie.setTitle("Original Title");
        movie.setLanguage("English");
        movie.setDuration(120);
        movie.setReleaseDate(LocalDate.of(2026, 1, 1));

        Movie savedMovie = movieRepository.save(movie);

        Integer movieId = savedMovie.getMovieId();

        savedMovie.setTitle("Updated Title");
        savedMovie.setDuration(150);

        movieRepository.save(savedMovie);

        Movie updatedMovie =
                movieRepository.findById(movieId)
                        .orElseThrow();

        assertEquals("Updated Title", updatedMovie.getTitle());
        assertEquals(150, updatedMovie.getDuration());
    }

    @Test
    void shouldDeleteMovie() {

        Movie movie = new Movie();

        movie.setTitle("Movie To Delete");
        movie.setLanguage("English");
        movie.setDuration(100);
        movie.setReleaseDate(LocalDate.of(2026, 1, 1));

        Movie savedMovie = movieRepository.save(movie);

        Integer movieId = savedMovie.getMovieId();

        assertNotNull(
                movieRepository.findById(movieId).orElse(null)
        );

        movieRepository.deleteById(movieId);

        assertFalse(movieRepository.existsById(movieId));
    }

    @Test
    void shouldSaveMovieWithNullableFields() {

        Movie movie = new Movie();

        movie.setTitle("Minimal Movie");
        movie.setLanguage("English");
        movie.setDuration(100);
        movie.setReleaseDate(LocalDate.of(2026, 1, 1));

        Movie savedMovie = movieRepository.save(movie);

        Movie retrievedMovie =
                movieRepository.findById(savedMovie.getMovieId())
                        .orElseThrow();

        assertEquals("Minimal Movie", retrievedMovie.getTitle());
        assertEquals("English", retrievedMovie.getLanguage());

        assertNull(retrievedMovie.getPoster());
        assertNull(retrievedMovie.getGenre());
        assertNull(retrievedMovie.getImdbRating());
        assertNull(retrievedMovie.getCertificate());
        assertNull(retrievedMovie.getDirector());
        assertNull(retrievedMovie.getDescription());
    }

    @Test
    void shouldPersistCertificateCorrectly() {

        Movie movie = new Movie();

        movie.setTitle("Certificate Test");
        movie.setLanguage("English");
        movie.setDuration(120);
        movie.setReleaseDate(LocalDate.of(2026, 1, 1));
        movie.setCertificate(Certificate.UA_16_PLUS);

        Movie savedMovie = movieRepository.save(movie);

        Movie retrievedMovie =
                movieRepository.findById(savedMovie.getMovieId())
                        .orElseThrow();

        assertEquals(
                Certificate.UA_16_PLUS,
                retrievedMovie.getCertificate()
        );
    }

    @Test
    void shouldPersistIMDbRatingCorrectly() {

        Movie movie = new Movie();

        movie.setTitle("Rating Test");
        movie.setLanguage("English");
        movie.setDuration(120);
        movie.setReleaseDate(LocalDate.of(2026, 1, 1));
        movie.setImdbRating(new BigDecimal("8.7"));

        Movie savedMovie = movieRepository.save(movie);

        Movie retrievedMovie =
                movieRepository.findById(savedMovie.getMovieId())
                        .orElseThrow();

        assertEquals(
                new BigDecimal("8.7"),
                retrievedMovie.getImdbRating()
        );
    }

    @Test
    void shouldRejectMovieWithoutTitle() {

        Movie movie = new Movie();

        movie.setLanguage("English");
        movie.setDuration(120);
        movie.setReleaseDate(LocalDate.of(2026, 1, 1));

        assertThrows(
                Exception.class,
                () -> {
                    movieRepository.saveAndFlush(movie);
                }
        );
    }

    @Test
    void shouldRejectMovieWithoutLanguage() {

        Movie movie = new Movie();

        movie.setTitle("No Language Movie");
        movie.setDuration(120);
        movie.setReleaseDate(LocalDate.of(2026, 1, 1));

        assertThrows(
                Exception.class,
                () -> movieRepository.saveAndFlush(movie)
        );
    }

    @Test
    void shouldPersistMovieDurationCorrectly() {

        Movie movie = new Movie();

        movie.setTitle("Duration Test");
        movie.setLanguage("English");
        movie.setDuration(1);
        movie.setReleaseDate(LocalDate.of(2026, 1, 1));

        Movie savedMovie = movieRepository.save(movie);

        Movie retrievedMovie =
                movieRepository.findById(savedMovie.getMovieId())
                        .orElseThrow();

        assertEquals(1, retrievedMovie.getDuration());
    }

    @Test
    void shouldRejectTitleExceedingMaximumLength() {

        Movie movie = new Movie();

        movie.setTitle(
                "This movie title is deliberately made longer than fifty characters"
        );
        movie.setLanguage("English");
        movie.setDuration(120);
        movie.setReleaseDate(LocalDate.of(2026, 1, 1));

        assertThrows(
                Exception.class,
                () -> movieRepository.saveAndFlush(movie)
        );
    }

    @Test
    void shouldRejectLanguageExceedingMaximumLength() {

        Movie movie = new Movie();

        movie.setTitle("Language Length Test");
        movie.setLanguage("This language name is deliberately too long");
        movie.setDuration(120);
        movie.setReleaseDate(LocalDate.of(2026, 1, 1));

        assertThrows(
                Exception.class,
                () -> movieRepository.saveAndFlush(movie)
        );
    }
}