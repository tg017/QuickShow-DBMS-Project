package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickshow.dbms.project.model.Movie;
import quickshow.dbms.project.model.Screen;
import quickshow.dbms.project.model.ScreenType;
import quickshow.dbms.project.model.Show;
import quickshow.dbms.project.model.ShowStatus;
import quickshow.dbms.project.model.Theatre;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShowRepositoryTest {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private TheatreRepository theatreRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateShow() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        Show show =
                createShow(
                        LocalDate.of(2026, 9, 1),
                        LocalTime.of(18, 30),
                        250L,
                        200,
                        ShowStatus.SCHEDULED
                );

        Show createdShow =
                showRepository.create(
                        show,
                        movie.getMovieId(),
                        screen.getScreenId()
                );

        assertNotNull(createdShow);
        assertNotNull(createdShow.getShowId());

        assertEquals(
                LocalDate.of(2026, 9, 1),
                createdShow.getShowDate()
        );

        assertEquals(
                LocalTime.of(18, 30),
                createdShow.getShowTime()
        );

        assertEquals(
                250L,
                createdShow.getTicketPrice()
        );

        assertEquals(
                200,
                createdShow.getAvailableSeats()
        );

        assertEquals(
                ShowStatus.SCHEDULED,
                createdShow.getShowStatus()
        );
    }


    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldCreateAndRetrieveShowById() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        Show show =
                createShow(
                        LocalDate.of(2026, 10, 15),
                        LocalTime.of(21, 45),
                        9999999999L,
                        350,
                        ShowStatus.ONGOING
                );

        Show createdShow =
                showRepository.create(
                        show,
                        movie.getMovieId(),
                        screen.getScreenId()
                );

        Show retrievedShow =
                showRepository.findById(
                        createdShow.getShowId()
                );

        assertNotNull(retrievedShow);

        assertEquals(
                createdShow.getShowId(),
                retrievedShow.getShowId()
        );

        assertEquals(
                LocalDate.of(2026, 10, 15),
                retrievedShow.getShowDate()
        );

        assertEquals(
                LocalTime.of(21, 45),
                retrievedShow.getShowTime()
        );

        assertEquals(
                9999999999L,
                retrievedShow.getTicketPrice()
        );

        assertEquals(
                350,
                retrievedShow.getAvailableSeats()
        );

        assertEquals(
                ShowStatus.ONGOING,
                retrievedShow.getShowStatus()
        );
    }


    // =========================================================
    // FIND NON-EXISTENT
    // =========================================================

    @Test
    void shouldReturnNullForNonExistentShow() {

        Show show =
                showRepository.findById(
                        Integer.MAX_VALUE
                );

        assertNull(show);
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindAllShows() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        Show show1 =
                showRepository.create(
                        createShow(
                                LocalDate.of(2026, 9, 1),
                                LocalTime.of(10, 0),
                                200L,
                                100,
                                ShowStatus.SCHEDULED
                        ),
                        movie.getMovieId(),
                        screen.getScreenId()
                );

        Show show2 =
                showRepository.create(
                        createShow(
                                LocalDate.of(2026, 9, 2),
                                LocalTime.of(14, 0),
                                300L,
                                80,
                                ShowStatus.ONGOING
                        ),
                        movie.getMovieId(),
                        screen.getScreenId()
                );

        List<Show> shows =
                showRepository.findAll();

        assertTrue(
                shows.stream()
                        .anyMatch(show ->
                                show.getShowId()
                                        .equals(show1.getShowId())
                        )
        );

        assertTrue(
                shows.stream()
                        .anyMatch(show ->
                                show.getShowId()
                                        .equals(show2.getShowId())
                        )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateShow() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        Show show =
                showRepository.create(
                        createShow(
                                LocalDate.of(2026, 9, 1),
                                LocalTime.of(18, 0),
                                200L,
                                200,
                                ShowStatus.SCHEDULED
                        ),
                        movie.getMovieId(),
                        screen.getScreenId()
                );

        show.setShowDate(
                LocalDate.of(2026, 9, 5)
        );

        show.setShowTime(
                LocalTime.of(22, 30)
        );

        show.setTicketPrice(500L);

        show.setAvailableSeats(150);

        show.setShowStatus(
                ShowStatus.CANCELLED
        );

        int rowsUpdated =
                showRepository.update(show);

        assertEquals(1, rowsUpdated);

        Show updatedShow =
                showRepository.findById(
                        show.getShowId()
                );

        assertNotNull(updatedShow);

        assertEquals(
                LocalDate.of(2026, 9, 5),
                updatedShow.getShowDate()
        );

        assertEquals(
                LocalTime.of(22, 30),
                updatedShow.getShowTime()
        );

        assertEquals(
                500L,
                updatedShow.getTicketPrice()
        );

        assertEquals(
                150,
                updatedShow.getAvailableSeats()
        );

        assertEquals(
                ShowStatus.CANCELLED,
                updatedShow.getShowStatus()
        );
    }


    // =========================================================
    // UPDATE NON-EXISTENT
    // =========================================================

    @Test
    void shouldUpdateZeroRowsForNonExistentShow() {

        Show show =
                createShow(
                        LocalDate.now(),
                        LocalTime.NOON,
                        100L,
                        100,
                        ShowStatus.SCHEDULED
                );

        show.setShowId(
                Integer.MAX_VALUE
        );

        int rowsUpdated =
                showRepository.update(show);

        assertEquals(0, rowsUpdated);
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteShow() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        Show show =
                showRepository.create(
                        createShow(
                                LocalDate.of(2026, 9, 1),
                                LocalTime.NOON,
                                200L,
                                100,
                                ShowStatus.SCHEDULED
                        ),
                        movie.getMovieId(),
                        screen.getScreenId()
                );

        Integer showId =
                show.getShowId();

        assertTrue(
                showRepository.existsById(showId)
        );

        int rowsDeleted =
                showRepository.deleteById(showId);

        assertEquals(1, rowsDeleted);

        assertFalse(
                showRepository.existsById(showId)
        );

        assertNull(
                showRepository.findById(showId)
        );
    }


    // =========================================================
    // DELETE NON-EXISTENT
    // =========================================================

    @Test
    void shouldDeleteZeroRowsForNonExistentShow() {

        int rowsDeleted =
                showRepository.deleteById(
                        Integer.MAX_VALUE
                );

        assertEquals(0, rowsDeleted);
    }


    // =========================================================
    // EXISTS
    // =========================================================

    @Test
    void shouldCheckShowExistence() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        Show show =
                showRepository.create(
                        createShow(
                                LocalDate.of(2026, 9, 1),
                                LocalTime.NOON,
                                200L,
                                100,
                                ShowStatus.SCHEDULED
                        ),
                        movie.getMovieId(),
                        screen.getScreenId()
                );

        assertTrue(
                showRepository.existsById(
                        show.getShowId()
                )
        );

        assertFalse(
                showRepository.existsById(
                        Integer.MAX_VALUE
                )
        );
    }


    // =========================================================
    // ALL SHOW STATUS VALUES
    // =========================================================

    @Test
    void shouldPersistEveryShowStatus() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        for (ShowStatus status :
                ShowStatus.values()) {

            Show show =
                    showRepository.create(
                            createShow(
                                    LocalDate.of(2026, 11, 1),
                                    LocalTime.NOON,
                                    100L,
                                    100,
                                    status
                            ),
                            movie.getMovieId(),
                            screen.getScreenId()
                    );

            Show retrieved =
                    showRepository.findById(
                            show.getShowId()
                    );

            assertNotNull(retrieved);

            assertEquals(
                    status,
                    retrieved.getShowStatus()
            );
        }
    }


    // =========================================================
    // FIND BY MOVIE
    // =========================================================

    @Test
    void shouldFindShowsByMovie() {

        Movie movie1 =
                createAndSaveMovie();

        Movie movie2 =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        Show show1 =
                showRepository.create(
                        createShow(
                                LocalDate.of(2026, 9, 1),
                                LocalTime.NOON,
                                100L,
                                100,
                                ShowStatus.SCHEDULED
                        ),
                        movie1.getMovieId(),
                        screen.getScreenId()
                );

        Show show2 =
                showRepository.create(
                        createShow(
                                LocalDate.of(2026, 9, 2),
                                LocalTime.NOON,
                                100L,
                                100,
                                ShowStatus.SCHEDULED
                        ),
                        movie2.getMovieId(),
                        screen.getScreenId()
                );

        List<Show> results =
                showRepository.findByMovie(
                        movie1.getMovieId()
                );

        assertTrue(
                results.stream()
                        .anyMatch(show ->
                                show.getShowId()
                                        .equals(show1.getShowId())
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(show ->
                                show.getShowId()
                                        .equals(show2.getShowId())
                        )
        );
    }


    // =========================================================
    // FIND BY SCREEN
    // =========================================================

    @Test
    void shouldFindShowsByScreen() {

        Movie movie =
                createAndSaveMovie();

        Screen screen1 =
                createAndSaveScreen();

        Screen screen2 =
                createAndSaveScreen();

        Show show1 =
                showRepository.create(
                        createShow(
                                LocalDate.of(2026, 9, 1),
                                LocalTime.NOON,
                                100L,
                                100,
                                ShowStatus.SCHEDULED
                        ),
                        movie.getMovieId(),
                        screen1.getScreenId()
                );

        Show show2 =
                showRepository.create(
                        createShow(
                                LocalDate.of(2026, 9, 2),
                                LocalTime.NOON,
                                100L,
                                100,
                                ShowStatus.SCHEDULED
                        ),
                        movie.getMovieId(),
                        screen2.getScreenId()
                );

        List<Show> results =
                showRepository.findByScreen(
                        screen1.getScreenId()
                );

        assertTrue(
                results.stream()
                        .anyMatch(show ->
                                show.getShowId()
                                        .equals(show1.getShowId())
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(show ->
                                show.getShowId()
                                        .equals(show2.getShowId())
                        )
        );
    }


    // =========================================================
    // FIND BY STATUS
    // =========================================================

    @Test
    void shouldFindShowsByStatus() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        Show scheduled =
                showRepository.create(
                        createShow(
                                LocalDate.of(2026, 9, 1),
                                LocalTime.NOON,
                                100L,
                                100,
                                ShowStatus.SCHEDULED
                        ),
                        movie.getMovieId(),
                        screen.getScreenId()
                );

        Show cancelled =
                showRepository.create(
                        createShow(
                                LocalDate.of(2026, 9, 2),
                                LocalTime.NOON,
                                100L,
                                100,
                                ShowStatus.CANCELLED
                        ),
                        movie.getMovieId(),
                        screen.getScreenId()
                );

        List<Show> results =
                showRepository.findByStatus(
                        ShowStatus.SCHEDULED
                );

        assertTrue(
                results.stream()
                        .anyMatch(show ->
                                show.getShowId()
                                        .equals(scheduled.getShowId())
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(show ->
                                show.getShowId()
                                        .equals(cancelled.getShowId())
                        )
        );
    }


    // =========================================================
    // FIND BY SCREEN + DATE
    // =========================================================

    @Test
    void shouldFindShowsByScreenAndDate() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        Show show1 =
                showRepository.create(
                        createShow(
                                LocalDate.of(2026, 12, 25),
                                LocalTime.of(10, 0),
                                100L,
                                100,
                                ShowStatus.SCHEDULED
                        ),
                        movie.getMovieId(),
                        screen.getScreenId()
                );

        Show show2 =
                showRepository.create(
                        createShow(
                                LocalDate.of(2026, 12, 25),
                                LocalTime.of(20, 0),
                                100L,
                                100,
                                ShowStatus.SCHEDULED
                        ),
                        movie.getMovieId(),
                        screen.getScreenId()
                );

        Show differentDate =
                showRepository.create(
                        createShow(
                                LocalDate.of(2026, 12, 26),
                                LocalTime.of(20, 0),
                                100L,
                                100,
                                ShowStatus.SCHEDULED
                        ),
                        movie.getMovieId(),
                        screen.getScreenId()
                );

        List<Show> results =
                showRepository.findByScreenAndDate(
                        screen.getScreenId(),
                        LocalDate.of(2026, 12, 25)
                );

        assertTrue(
                results.stream()
                        .anyMatch(show ->
                                show.getShowId()
                                        .equals(show1.getShowId())
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(show ->
                                show.getShowId()
                                        .equals(show2.getShowId())
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(show ->
                                show.getShowId()
                                        .equals(
                                                differentDate.getShowId()
                                        )
                        )
        );

        assertEquals(
                LocalTime.of(10, 0),
                results.get(0).getShowTime()
        );

        assertEquals(
                LocalTime.of(20, 0),
                results.get(1).getShowTime()
        );
    }


    // =========================================================
    // NULL SHOW DATE
    // =========================================================

    @Test
    void shouldRejectNullShowDate() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        Show show =
                createShow(
                        null,
                        LocalTime.NOON,
                        100L,
                        100,
                        ShowStatus.SCHEDULED
                );

        assertThrows(
                Exception.class,
                () -> showRepository.create(
                        show,
                        movie.getMovieId(),
                        screen.getScreenId()
                )
        );
    }


    // =========================================================
    // NULL SHOW TIME
    // =========================================================

    @Test
    void shouldRejectNullShowTime() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        Show show =
                createShow(
                        LocalDate.of(2026, 9, 1),
                        null,
                        100L,
                        100,
                        ShowStatus.SCHEDULED
                );

        assertThrows(
                Exception.class,
                () -> showRepository.create(
                        show,
                        movie.getMovieId(),
                        screen.getScreenId()
                )
        );
    }


    // =========================================================
    // NULL TICKET PRICE
    // =========================================================

    @Test
    void shouldRejectNullTicketPrice() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        Show show =
                createShow(
                        LocalDate.of(2026, 9, 1),
                        LocalTime.NOON,
                        null,
                        100,
                        ShowStatus.SCHEDULED
                );

        assertThrows(
                Exception.class,
                () -> showRepository.create(
                        show,
                        movie.getMovieId(),
                        screen.getScreenId()
                )
        );
    }


    // =========================================================
    // NULL AVAILABLE SEATS
    // =========================================================

    @Test
    void shouldRejectNullAvailableSeats() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        Show show =
                createShow(
                        LocalDate.of(2026, 9, 1),
                        LocalTime.NOON,
                        100L,
                        null,
                        ShowStatus.SCHEDULED
                );

        assertThrows(
                Exception.class,
                () -> showRepository.create(
                        show,
                        movie.getMovieId(),
                        screen.getScreenId()
                )
        );
    }


    // =========================================================
    // NULL STATUS
    // =========================================================

    @Test
    void shouldRejectNullShowStatus() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        Show show =
                createShow(
                        LocalDate.of(2026, 9, 1),
                        LocalTime.NOON,
                        100L,
                        100,
                        null
                );

        assertThrows(
                Exception.class,
                () -> showRepository.create(
                        show,
                        movie.getMovieId(),
                        screen.getScreenId()
                )
        );
    }


    // =========================================================
    // INVALID MOVIE FK
    // =========================================================

    @Test
    void shouldRejectNonExistentMovie() {

        Screen screen =
                createAndSaveScreen();

        Show show =
                createShow(
                        LocalDate.of(2026, 9, 1),
                        LocalTime.NOON,
                        100L,
                        100,
                        ShowStatus.SCHEDULED
                );

        assertThrows(
                Exception.class,
                () -> showRepository.create(
                        show,
                        Integer.MAX_VALUE,
                        screen.getScreenId()
                )
        );
    }


    // =========================================================
    // INVALID SCREEN FK
    // =========================================================

    @Test
    void shouldRejectNonExistentScreen() {

        Movie movie =
                createAndSaveMovie();

        Show show =
                createShow(
                        LocalDate.of(2026, 9, 1),
                        LocalTime.NOON,
                        100L,
                        100,
                        ShowStatus.SCHEDULED
                );

        assertThrows(
                Exception.class,
                () -> showRepository.create(
                        show,
                        movie.getMovieId(),
                        Integer.MAX_VALUE
                )
        );
    }


    // =========================================================
    // HELPERS
    // =========================================================

    private Movie createAndSaveMovie() {

        Movie movie = new Movie();

        movie.setTitle(
                "Show Test Movie"
        );

        movie.setPoster(
                "show-test.jpg"
        );

        movie.setLanguage(
                "English"
        );

        movie.setGenre(
                "Sci-Fi"
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
                "Movie used for Show repository tests."
        );

        return movieRepository.create(movie);
    }


    private Screen createAndSaveScreen() {

        Theatre theatre =
                new Theatre();

        theatre.setName(
                "Show Test Theatre"
        );

        theatre.setContactNo(
                "9876543210"
        );

        theatre.setBuildingName(
                "Test Building"
        );

        theatre.setStreet(
                "Test Street"
        );

        theatre.setArea(
                "Test Area"
        );

        theatre.setCity(
                "Mumbai"
        );

        theatre.setState(
                "Maharashtra"
        );

        theatre.setPinCode(
                "400001"
        );

        Theatre savedTheatre =
                theatreRepository.create(
                        theatre
                );

        Screen screen =
                new Screen();

        screen.setName(
                "Show Test Screen"
        );

        screen.setScreenType(
                ScreenType.TWO_D
        );

        screen.setCapacity(200);

        return screenRepository.create(
                screen,
                savedTheatre.getTheatreId()
        );
    }


    private Show createShow(
            LocalDate date,
            LocalTime time,
            Long ticketPrice,
            Integer availableSeats,
            ShowStatus status
    ) {

        Show show = new Show();

        show.setShowDate(date);
        show.setShowTime(time);
        show.setTicketPrice(ticketPrice);
        show.setAvailableSeats(availableSeats);
        show.setShowStatus(status);

        return show;
    }
}