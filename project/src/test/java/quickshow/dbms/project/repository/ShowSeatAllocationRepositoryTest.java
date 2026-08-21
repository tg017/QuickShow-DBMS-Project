package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickshow.dbms.project.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShowSeatAllocationRepositoryTest {

    @Autowired
    private ShowSeatAllocationRepository allocationRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private MovieRepository movieRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateAllocation() {

        TestData data = createTestData();

        ShowSeatAllocation allocation =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        SeatAllocationStatus.AVAILABLE
                );

        ShowSeatAllocation created =
                allocationRepository.create(allocation);

        assertNotNull(created);
        assertNotNull(created.getId());

        assertEquals(
                data.show.getShowId(),
                created.getId().getShowId()
        );

        assertEquals(
                data.screen.getScreenId(),
                created.getId().getScreenId()
        );

        assertEquals(
                1,
                created.getId().getSeatId()
        );

        assertEquals(
                SeatAllocationStatus.AVAILABLE,
                created.getStatus()
        );
    }


    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldCreateAndRetrieveAllocationByCompositeId() {

        TestData data = createTestData();

        ShowSeatAllocation allocation =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        SeatAllocationStatus.BOOKED
                );

        allocationRepository.create(allocation);

        ShowSeatAllocation retrieved =
                allocationRepository.findById(
                        allocation.getId()
                );

        assertNotNull(retrieved);

        assertEquals(
                allocation.getId(),
                retrieved.getId()
        );

        assertEquals(
                SeatAllocationStatus.BOOKED,
                retrieved.getStatus()
        );
    }


    // =========================================================
    // FIND NON-EXISTENT
    // =========================================================

    @Test
    void shouldReturnNullForNonExistentAllocation() {

        ShowSeatAllocationId id =
                new ShowSeatAllocationId(
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                );

        assertNull(
                allocationRepository.findById(id)
        );
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindAllAllocations() {

        TestData data = createTestData();

        ShowSeatAllocation allocation1 =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        SeatAllocationStatus.AVAILABLE
                );

        ShowSeatAllocation allocation2 =
                createAllocation(
                        data.show,
                        data.screen,
                        2,
                        SeatAllocationStatus.BOOKED
                );

        allocationRepository.create(allocation1);
        allocationRepository.create(allocation2);

        List<ShowSeatAllocation> results =
                allocationRepository.findAll();

        assertTrue(
                results.stream()
                        .anyMatch(a ->
                                a.getId().equals(
                                        allocation1.getId()
                                )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(a ->
                                a.getId().equals(
                                        allocation2.getId()
                                )
                        )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateAllocationStatus() {

        TestData data = createTestData();

        ShowSeatAllocation allocation =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        SeatAllocationStatus.AVAILABLE
                );

        allocationRepository.create(allocation);

        allocation.setStatus(
                SeatAllocationStatus.BOOKED
        );

        int rowsUpdated =
                allocationRepository.update(allocation);

        assertEquals(1, rowsUpdated);

        ShowSeatAllocation updated =
                allocationRepository.findById(
                        allocation.getId()
                );

        assertNotNull(updated);

        assertEquals(
                SeatAllocationStatus.BOOKED,
                updated.getStatus()
        );
    }


    // =========================================================
    // UPDATE NON-EXISTENT
    // =========================================================

    @Test
    void shouldUpdateZeroRowsForNonExistentAllocation() {

        ShowSeatAllocation allocation =
                new ShowSeatAllocation();

        allocation.setId(
                new ShowSeatAllocationId(
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                )
        );

        allocation.setStatus(
                SeatAllocationStatus.BOOKED
        );

        assertEquals(
                0,
                allocationRepository.update(allocation)
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteAllocation() {

        TestData data = createTestData();

        ShowSeatAllocation allocation =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        SeatAllocationStatus.AVAILABLE
                );

        allocationRepository.create(allocation);

        assertTrue(
                allocationRepository.existsById(
                        allocation.getId()
                )
        );

        assertEquals(
                1,
                allocationRepository.deleteById(
                        allocation.getId()
                )
        );

        assertFalse(
                allocationRepository.existsById(
                        allocation.getId()
                )
        );

        assertNull(
                allocationRepository.findById(
                        allocation.getId()
                )
        );
    }


    // =========================================================
    // DELETE NON-EXISTENT
    // =========================================================

    @Test
    void shouldDeleteZeroRowsForNonExistentAllocation() {

        ShowSeatAllocationId id =
                new ShowSeatAllocationId(
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                );

        assertEquals(
                0,
                allocationRepository.deleteById(id)
        );
    }


    // =========================================================
    // EXISTS
    // =========================================================

    @Test
    void shouldCheckAllocationExistence() {

        TestData data = createTestData();

        ShowSeatAllocation allocation =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        SeatAllocationStatus.AVAILABLE
                );

        allocationRepository.create(allocation);

        assertTrue(
                allocationRepository.existsById(
                        allocation.getId()
                )
        );

        assertFalse(
                allocationRepository.existsById(
                        new ShowSeatAllocationId(
                                Integer.MAX_VALUE,
                                Integer.MAX_VALUE,
                                Integer.MAX_VALUE
                        )
                )
        );
    }


    // =========================================================
    // DUPLICATE COMPOSITE PRIMARY KEY
    // =========================================================

    @Test
    void shouldRejectDuplicateCompositeKey() {

        TestData data = createTestData();

        ShowSeatAllocation allocation1 =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        SeatAllocationStatus.AVAILABLE
                );

        allocationRepository.create(allocation1);

        // Same ShowID + ScreenID + SeatID
        ShowSeatAllocation allocation2 =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        SeatAllocationStatus.BOOKED
                );

        assertThrows(
                Exception.class,
                () -> allocationRepository.create(
                        allocation2
                )
        );
    }


    // =========================================================
    // SAME SEAT ON DIFFERENT SHOWS
    // =========================================================

    @Test
    void shouldAllowSameSeatOnDifferentShows() {

        TestData data = createTestData();

        Show secondShow =
                createAndSaveShow(
                        data.movie,
                        data.screen
                );

        ShowSeatAllocation allocation1 =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        SeatAllocationStatus.AVAILABLE
                );

        ShowSeatAllocation allocation2 =
                createAllocation(
                        secondShow,
                        data.screen,
                        1,
                        SeatAllocationStatus.AVAILABLE
                );

        assertDoesNotThrow(
                () -> allocationRepository.create(
                        allocation1
                )
        );

        assertDoesNotThrow(
                () -> allocationRepository.create(
                        allocation2
                )
        );
    }


    // =========================================================
    // INVALID SHOW FK
    // =========================================================

    @Test
    void shouldRejectNonExistentShow() {

        TestData data = createTestData();

        ShowSeatAllocation allocation =
                new ShowSeatAllocation();

        allocation.setId(
                new ShowSeatAllocationId(
                        Integer.MAX_VALUE,
                        data.screen.getScreenId(),
                        1
                )
        );

        allocation.setStatus(
                SeatAllocationStatus.AVAILABLE
        );

        assertThrows(
                Exception.class,
                () -> allocationRepository.create(
                        allocation
                )
        );
    }


    // =========================================================
    // INVALID SEAT FK
    // =========================================================

    @Test
    void shouldRejectNonExistentSeat() {

        TestData data = createTestData();

        ShowSeatAllocation allocation =
                new ShowSeatAllocation();

        allocation.setId(
                new ShowSeatAllocationId(
                        data.show.getShowId(),
                        data.screen.getScreenId(),
                        Integer.MAX_VALUE
                )
        );

        allocation.setStatus(
                SeatAllocationStatus.AVAILABLE
        );

        assertThrows(
                Exception.class,
                () -> allocationRepository.create(
                        allocation
                )
        );
    }


    // =========================================================
    // INVALID SCREEN + SEAT COMBINATION
    // =========================================================

    @Test
    void shouldRejectInvalidScreenSeatCombination() {

        TestData data = createTestData();

        Screen anotherScreen =
                createAndSaveScreen();

        ShowSeatAllocation allocation =
                new ShowSeatAllocation();

        /*
         * SeatID = 1 belongs to data.screen.
         *
         * We deliberately use anotherScreen's ScreenID.
         *
         * Therefore (anotherScreen, 1) does not exist
         * in Seat.
         */

        allocation.setId(
                new ShowSeatAllocationId(
                        data.show.getShowId(),
                        anotherScreen.getScreenId(),
                        1
                )
        );

        allocation.setStatus(
                SeatAllocationStatus.AVAILABLE
        );

        assertThrows(
                Exception.class,
                () -> allocationRepository.create(
                        allocation
                )
        );
    }


    // =========================================================
    // AVAILABLE STATUS
    // =========================================================

    @Test
    void shouldPersistAvailableStatus() {

        TestData data = createTestData();

        ShowSeatAllocation allocation =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        SeatAllocationStatus.AVAILABLE
                );

        allocationRepository.create(allocation);

        ShowSeatAllocation retrieved =
                allocationRepository.findById(
                        allocation.getId()
                );

        assertEquals(
                SeatAllocationStatus.AVAILABLE,
                retrieved.getStatus()
        );
    }


    // =========================================================
    // BOOKED STATUS
    // =========================================================

    @Test
    void shouldPersistBookedStatus() {

        TestData data = createTestData();

        ShowSeatAllocation allocation =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        SeatAllocationStatus.BOOKED
                );

        allocationRepository.create(allocation);

        ShowSeatAllocation retrieved =
                allocationRepository.findById(
                        allocation.getId()
                );

        assertEquals(
                SeatAllocationStatus.BOOKED,
                retrieved.getStatus()
        );
    }


    // =========================================================
    // FIND BY SHOW
    // =========================================================

    @Test
    void shouldFindAllocationsByShow() {

        TestData data = createTestData();

        ShowSeatAllocation allocation1 =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        SeatAllocationStatus.AVAILABLE
                );

        ShowSeatAllocation allocation2 =
                createAllocation(
                        data.show,
                        data.screen,
                        2,
                        SeatAllocationStatus.BOOKED
                );

        Show secondShow =
                createAndSaveShow(
                        data.movie,
                        data.screen
                );

        ShowSeatAllocation allocation3 =
                createAllocation(
                        secondShow,
                        data.screen,
                        1,
                        SeatAllocationStatus.AVAILABLE
                );

        allocationRepository.create(allocation1);
        allocationRepository.create(allocation2);
        allocationRepository.create(allocation3);

        List<ShowSeatAllocation> results =
                allocationRepository.findByShow(
                        data.show.getShowId()
                );

        assertTrue(
                results.stream()
                        .anyMatch(a ->
                                a.getId().equals(
                                        allocation1.getId()
                                )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(a ->
                                a.getId().equals(
                                        allocation2.getId()
                                )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(a ->
                                a.getId().equals(
                                        allocation3.getId()
                                )
                        )
        );
    }


    // =========================================================
    // FIND BY SEAT
    // =========================================================

    @Test
    void shouldFindAllocationsBySeat() {

        TestData data = createTestData();

        Show secondShow =
                createAndSaveShow(
                        data.movie,
                        data.screen
                );

        ShowSeatAllocation allocation1 =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        SeatAllocationStatus.AVAILABLE
                );

        ShowSeatAllocation allocation2 =
                createAllocation(
                        secondShow,
                        data.screen,
                        1,
                        SeatAllocationStatus.BOOKED
                );

        allocationRepository.create(allocation1);
        allocationRepository.create(allocation2);

        List<ShowSeatAllocation> results =
                allocationRepository.findBySeat(
                        data.screen.getScreenId(),
                        1
                );

        assertTrue(
                results.stream()
                        .anyMatch(a ->
                                a.getId().equals(
                                        allocation1.getId()
                                )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(a ->
                                a.getId().equals(
                                        allocation2.getId()
                                )
                        )
        );
    }


    // =========================================================
    // FIND BY SHOW + STATUS
    // =========================================================

    @Test
    void shouldFindAllocationsByShowAndStatus() {

        TestData data = createTestData();

        ShowSeatAllocation available =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        SeatAllocationStatus.AVAILABLE
                );

        ShowSeatAllocation booked =
                createAllocation(
                        data.show,
                        data.screen,
                        2,
                        SeatAllocationStatus.BOOKED
                );

        allocationRepository.create(available);
        allocationRepository.create(booked);

        List<ShowSeatAllocation> results =
                allocationRepository.findByShowAndStatus(
                        data.show.getShowId(),
                        SeatAllocationStatus.BOOKED
                );

        assertTrue(
                results.stream()
                        .anyMatch(a ->
                                a.getId().equals(
                                        booked.getId()
                                )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(a ->
                                a.getId().equals(
                                        available.getId()
                                )
                        )
        );
    }


    // =========================================================
    // FIND BY SHOW + SCREEN
    // =========================================================

    @Test
    void shouldFindAllocationsByShowAndScreen() {

        TestData data = createTestData();

        ShowSeatAllocation allocation1 =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        SeatAllocationStatus.AVAILABLE
                );

        ShowSeatAllocation allocation2 =
                createAllocation(
                        data.show,
                        data.screen,
                        2,
                        SeatAllocationStatus.BOOKED
                );

        allocationRepository.create(allocation1);
        allocationRepository.create(allocation2);

        List<ShowSeatAllocation> results =
                allocationRepository.findByShowAndScreen(
                        data.show.getShowId(),
                        data.screen.getScreenId()
                );

        assertTrue(
                results.stream()
                        .anyMatch(a ->
                                a.getId().equals(
                                        allocation1.getId()
                                )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(a ->
                                a.getId().equals(
                                        allocation2.getId()
                                )
                        )
        );
    }


    // =========================================================
    // NULL STATUS
    // =========================================================

    @Test
    void shouldRejectNullStatus() {

        TestData data = createTestData();

        ShowSeatAllocation allocation =
                createAllocation(
                        data.show,
                        data.screen,
                        1,
                        null
                );

        assertThrows(
                Exception.class,
                () -> allocationRepository.create(
                        allocation
                )
        );
    }


    // =========================================================
    // HELPERS
    // =========================================================

    private TestData createTestData() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        /*
         * These seats are required because
         * ShowSeatAllocates has:
         *
         * FOREIGN KEY (ScreenID, SeatID)
         * REFERENCES Seat(ScreenID, SeatID)
         */

        createAndSaveSeat(screen, 1);
        createAndSaveSeat(screen, 2);
        createAndSaveSeat(screen, 10);

        Show show =
                createAndSaveShow(
                        movie,
                        screen
                );

        return new TestData(
                movie,
                screen,
                show
        );
    }


    private ShowSeatAllocation createAllocation(
            Show show,
            Screen screen,
            Integer seatId,
            SeatAllocationStatus status
    ) {

        ShowSeatAllocation allocation =
                new ShowSeatAllocation();

        allocation.setId(
                new ShowSeatAllocationId(
                        show.getShowId(),
                        screen.getScreenId(),
                        seatId
                )
        );

        allocation.setShow(show);
        allocation.setStatus(status);

        return allocation;
    }


    private void createAndSaveSeat(
            Screen screen,
            Integer seatId
    ) {

        Seat seat =
                new Seat();

        seat.setId(
                new SeatId(
                        screen.getScreenId(),
                        seatId
                )
        );

        seat.setScreen(screen);
        seat.setRowNo("A");
        seat.setSeatNo(seatId);

        seatRepository.create(seat);
    }


    private Movie createAndSaveMovie() {

        Movie movie =
                new Movie();

        movie.setTitle(
                "Allocation Test Movie"
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
                "Test movie for allocation repository."
        );

        return movieRepository.create(movie);
    }


    private Screen createAndSaveScreen() {

        Theatre theatre =
                new Theatre();

        theatre.setName(
                "Allocation Test Theatre"
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
                "Allocation Test Screen"
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


    private Show createAndSaveShow(
            Movie movie,
            Screen screen
    ) {

        Show show =
                new Show();

        show.setShowDate(
                LocalDate.of(2026, 12, 1)
        );

        show.setShowTime(
                LocalTime.of(18, 30)
        );

        show.setTicketPrice(250L);

        show.setAvailableSeats(200);

        show.setShowStatus(
                ShowStatus.SCHEDULED
        );

        return showRepository.create(
                show,
                movie.getMovieId(),
                screen.getScreenId()
        );
    }


    private static class TestData {

        private final Movie movie;
        private final Screen screen;
        private final Show show;

        private TestData(
                Movie movie,
                Screen screen,
                Show show
        ) {
            this.movie = movie;
            this.screen = screen;
            this.show = show;
        }
    }
}