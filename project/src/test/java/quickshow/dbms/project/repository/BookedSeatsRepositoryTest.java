package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickshow.dbms.project.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookedSeatsRepositoryTest {

    @Autowired
    private BookedSeatsRepository bookedSeatsRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

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
    void shouldCreateBookedSeat() {

        TestData data =
                createTestData();

        Booking booking =
                createBooking();

        BookedSeats bookedSeat =
                createBookedSeat(
                        booking,
                        data.allocation1
                );

        BookedSeats created =
                bookedSeatsRepository.create(
                        bookedSeat
                );

        assertNotNull(created);
        assertNotNull(created.getId());

        assertEquals(
                booking.getBookingId(),
                created.getId()
                        .getBookingId()
        );

        assertEquals(
                data.allocation1.getId()
                        .getShowId(),
                created.getId()
                        .getShowId()
        );

        assertEquals(
                data.allocation1.getId()
                        .getScreenId(),
                created.getId()
                        .getScreenId()
        );

        assertEquals(
                data.allocation1.getId()
                        .getSeatId(),
                created.getId()
                        .getSeatId()
        );
    }


    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldCreateAndRetrieveBookedSeatByCompositeId() {

        TestData data =
                createTestData();

        Booking booking =
                createBooking();

        BookedSeats bookedSeat =
                createBookedSeat(
                        booking,
                        data.allocation1
                );

        bookedSeatsRepository.create(
                bookedSeat
        );

        BookedSeats retrieved =
                bookedSeatsRepository.findById(
                        bookedSeat.getId()
                );

        assertNotNull(retrieved);

        assertEquals(
                bookedSeat.getId(),
                retrieved.getId()
        );
    }


    // =========================================================
    // FIND NON-EXISTENT
    // =========================================================

    @Test
    void shouldReturnNullForNonExistentBookedSeat() {

        BookedSeatsId id =
                new BookedSeatsId(
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                );

        assertNull(
                bookedSeatsRepository.findById(id)
        );
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindAllBookedSeats() {

        TestData data =
                createTestData();

        Booking booking =
                createBooking();

        BookedSeats bookedSeat1 =
                createBookedSeat(
                        booking,
                        data.allocation1
                );

        BookedSeats bookedSeat2 =
                createBookedSeat(
                        booking,
                        data.allocation2
                );

        BookedSeats saved1 =
                bookedSeatsRepository.create(
                        bookedSeat1
                );

        BookedSeats saved2 =
                bookedSeatsRepository.create(
                        bookedSeat2
                );

        List<BookedSeats> results =
                bookedSeatsRepository.findAll();

        assertTrue(
                results.stream()
                        .anyMatch(bookedSeat ->
                                bookedSeat.getId()
                                        .equals(
                                                saved1.getId()
                                        )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(bookedSeat ->
                                bookedSeat.getId()
                                        .equals(
                                                saved2.getId()
                                        )
                        )
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteBookedSeat() {

        TestData data =
                createTestData();

        Booking booking =
                createBooking();

        BookedSeats bookedSeat =
                bookedSeatsRepository.create(
                        createBookedSeat(
                                booking,
                                data.allocation1
                        )
                );

        assertTrue(
                bookedSeatsRepository.existsById(
                        bookedSeat.getId()
                )
        );

        int rowsDeleted =
                bookedSeatsRepository.deleteById(
                        bookedSeat.getId()
                );

        assertEquals(
                1,
                rowsDeleted
        );

        assertFalse(
                bookedSeatsRepository.existsById(
                        bookedSeat.getId()
                )
        );

        assertNull(
                bookedSeatsRepository.findById(
                        bookedSeat.getId()
                )
        );
    }


    // =========================================================
    // DELETE NON-EXISTENT
    // =========================================================

    @Test
    void shouldDeleteZeroRowsForNonExistentBookedSeat() {

        BookedSeatsId id =
                new BookedSeatsId(
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                );

        assertEquals(
                0,
                bookedSeatsRepository.deleteById(id)
        );
    }


    // =========================================================
    // EXISTS
    // =========================================================

    @Test
    void shouldCheckBookedSeatExistence() {

        TestData data =
                createTestData();

        Booking booking =
                createBooking();

        BookedSeats bookedSeat =
                bookedSeatsRepository.create(
                        createBookedSeat(
                                booking,
                                data.allocation1
                        )
                );

        assertTrue(
                bookedSeatsRepository.existsById(
                        bookedSeat.getId()
                )
        );

        assertFalse(
                bookedSeatsRepository.existsById(
                        new BookedSeatsId(
                                Integer.MAX_VALUE,
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

        TestData data =
                createTestData();

        Booking booking =
                createBooking();

        BookedSeats bookedSeat1 =
                createBookedSeat(
                        booking,
                        data.allocation1
                );

        bookedSeatsRepository.create(
                bookedSeat1
        );

        /*
         * Same BookingID + ShowID + ScreenID + SeatID
         */
        BookedSeats bookedSeat2 =
                createBookedSeat(
                        booking,
                        data.allocation1
                );

        assertThrows(
                Exception.class,
                () ->
                        bookedSeatsRepository.create(
                                bookedSeat2
                        )
        );
    }


    // =========================================================
    // SAME SEAT FOR DIFFERENT BOOKINGS
    // =========================================================

    @Test
    void shouldAllowSameSeatForDifferentBookings() {

        TestData data =
                createTestData();

        Booking booking1 =
                createBooking();

        Booking booking2 =
                createBooking();

        BookedSeats bookedSeat1 =
                createBookedSeat(
                        booking1,
                        data.allocation1
                );

        BookedSeats bookedSeat2 =
                createBookedSeat(
                        booking2,
                        data.allocation1
                );

        assertDoesNotThrow(
                () ->
                        bookedSeatsRepository.create(
                                bookedSeat1
                        )
        );

        assertDoesNotThrow(
                () ->
                        bookedSeatsRepository.create(
                                bookedSeat2
                        )
        );
    }


    // =========================================================
    // INVALID BOOKING FK
    // =========================================================

    @Test
    void shouldRejectNonExistentBooking() {

        TestData data =
                createTestData();

        BookedSeatsId id =
                new BookedSeatsId(
                        Integer.MAX_VALUE,
                        data.allocation1.getId()
                                .getShowId(),
                        data.allocation1.getId()
                                .getScreenId(),
                        data.allocation1.getId()
                                .getSeatId()
                );

        BookedSeats bookedSeat =
                new BookedSeats();

        bookedSeat.setId(id);

        assertThrows(
                Exception.class,
                () ->
                        bookedSeatsRepository.create(
                                bookedSeat
                        )
        );
    }


    // =========================================================
    // INVALID SHOW SEAT ALLOCATION FK
    // =========================================================

    @Test
    void shouldRejectNonExistentShowSeatAllocation() {

        Booking booking =
                createBooking();

        BookedSeatsId id =
                new BookedSeatsId(
                        booking.getBookingId(),
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                );

        BookedSeats bookedSeat =
                new BookedSeats();

        bookedSeat.setId(id);

        assertThrows(
                Exception.class,
                () ->
                        bookedSeatsRepository.create(
                                bookedSeat
                        )
        );
    }


    // =========================================================
    // FIND BY BOOKING
    // =========================================================

    @Test
    void shouldFindBookedSeatsByBooking() {

        TestData data =
                createTestData();

        Booking booking =
                createBooking();

        BookedSeats bookedSeat1 =
                createBookedSeat(
                        booking,
                        data.allocation1
                );

        BookedSeats bookedSeat2 =
                createBookedSeat(
                        booking,
                        data.allocation2
                );

        BookedSeats saved1 =
                bookedSeatsRepository.create(
                        bookedSeat1
                );

        BookedSeats saved2 =
                bookedSeatsRepository.create(
                        bookedSeat2
                );

        List<BookedSeats> results =
                bookedSeatsRepository.findByBooking(
                        booking.getBookingId()
                );

        assertTrue(
                results.stream()
                        .anyMatch(bookedSeat ->
                                bookedSeat.getId()
                                        .equals(
                                                saved1.getId()
                                        )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(bookedSeat ->
                                bookedSeat.getId()
                                        .equals(
                                                saved2.getId()
                                        )
                        )
        );

        assertTrue(
                results.stream()
                        .allMatch(bookedSeat ->
                                bookedSeat.getId()
                                        .getBookingId()
                                        .equals(
                                                booking
                                                        .getBookingId()
                                        )
                        )
        );
    }


    // =========================================================
    // FIND BY SHOW
    // =========================================================

    @Test
    void shouldFindBookedSeatsByShow() {

        TestData data =
                createTestData();

        Booking booking =
                createBooking();

        BookedSeats bookedSeat =
                createBookedSeat(
                        booking,
                        data.allocation1
                );

        bookedSeatsRepository.create(
                bookedSeat
        );

        List<BookedSeats> results =
                bookedSeatsRepository.findByShow(
                        data.show.getShowId()
                );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                result.getId()
                                        .equals(
                                                bookedSeat.getId()
                                        )
                        )
        );
    }


    // =========================================================
    // FIND BY BOOKING + SHOW
    // =========================================================

    @Test
    void shouldFindBookedSeatsByBookingAndShow() {

        TestData data =
                createTestData();

        Booking booking =
                createBooking();

        BookedSeats bookedSeat =
                createBookedSeat(
                        booking,
                        data.allocation1
                );

        bookedSeatsRepository.create(
                bookedSeat
        );

        List<BookedSeats> results =
                bookedSeatsRepository
                        .findByBookingAndShow(
                                booking.getBookingId(),
                                data.show.getShowId()
                        );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                result.getId()
                                        .equals(
                                                bookedSeat.getId()
                                        )
                        )
        );
    }


    // =========================================================
    // FIND BY SHOW + SCREEN
    // =========================================================

    @Test
    void shouldFindBookedSeatsByShowAndScreen() {

        TestData data =
                createTestData();

        Booking booking =
                createBooking();

        BookedSeats bookedSeat =
                createBookedSeat(
                        booking,
                        data.allocation1
                );

        bookedSeatsRepository.create(
                bookedSeat
        );

        List<BookedSeats> results =
                bookedSeatsRepository
                        .findByShowAndScreen(
                                data.show.getShowId(),
                                data.screen.getScreenId()
                        );

        assertTrue(
                results.stream()
                        .anyMatch(result ->
                                result.getId()
                                        .equals(
                                                bookedSeat.getId()
                                        )
                        )
        );
    }


    // =========================================================
    // DELETE ALL BY BOOKING
    // =========================================================

    @Test
    void shouldDeleteAllBookedSeatsOfBooking() {

        TestData data =
                createTestData();

        Booking booking =
                createBooking();

        bookedSeatsRepository.create(
                createBookedSeat(
                        booking,
                        data.allocation1
                )
        );

        bookedSeatsRepository.create(
                createBookedSeat(
                        booking,
                        data.allocation2
                )
        );

        int rowsDeleted =
                bookedSeatsRepository.deleteByBooking(
                        booking.getBookingId()
                );

        assertEquals(
                2,
                rowsDeleted
        );

        List<BookedSeats> results =
                bookedSeatsRepository.findByBooking(
                        booking.getBookingId()
                );

        assertTrue(
                results.isEmpty()
        );
    }


    // =========================================================
    // HELPERS
    // =========================================================

    private BookedSeats createBookedSeat(
            Booking booking,
            ShowSeatAllocation allocation
    ) {

        ShowSeatAllocationId allocationId =
                allocation.getId();

        BookedSeatsId id =
                new BookedSeatsId(
                        booking.getBookingId(),
                        allocationId.getShowId(),
                        allocationId.getScreenId(),
                        allocationId.getSeatId()
                );

        return new BookedSeats(
                id,
                booking,
                allocation
        );
    }


    private Booking createBooking() {

        Customer customer =
                new Customer();

        customer.setFirstName("Test");
        customer.setLastName("User");
        customer.setPassword("password");

        Customer savedCustomer =
                customerRepository.create(
                        customer
                );

        Booking booking =
                new Booking();

        booking.setCustomer(
                savedCustomer
        );

        booking.setBookingDateTime(
                LocalDateTime.of(
                        2026,
                        8,
                        21,
                        18,
                        30
                )
        );

        booking.setBookingStatus(
                BookingStatus.CONFIRMED
        );

        booking.setTotalAmount(
                500L
        );

        booking.setTotalSeatsCount(
                2
        );

        return bookingRepository.create(
                booking
        );
    }


    private TestData createTestData() {

        Movie movie =
                createAndSaveMovie();

        Screen screen =
                createAndSaveScreen();

        createAndSaveSeat(
                screen,
                1
        );

        createAndSaveSeat(
                screen,
                2
        );

        Show show =
                createAndSaveShow(
                        movie,
                        screen
                );

        ShowSeatAllocation allocation1 =
                createAndSaveAllocation(
                        show,
                        screen,
                        1
                );

        ShowSeatAllocation allocation2 =
                createAndSaveAllocation(
                        show,
                        screen,
                        2
                );

        return new TestData(
                movie,
                screen,
                show,
                allocation1,
                allocation2
        );
    }


    private ShowSeatAllocation createAndSaveAllocation(
            Show show,
            Screen screen,
            Integer seatId
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

        allocation.setStatus(
                SeatAllocationStatus.AVAILABLE
        );

        return allocationRepository.create(
                allocation
        );
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
                "Booked Seat Test Movie"
        );

        movie.setLanguage(
                "English"
        );

        movie.setGenre(
                "Sci-Fi"
        );

        movie.setDuration(120);

        movie.setReleaseDate(
                LocalDate.of(
                        2026,
                        1,
                        1
                )
        );

        movie.setImdbRating(
                new BigDecimal("8.5")
        );

        movie.setDirector(
                "Test Director"
        );

        movie.setDescription(
                "Test movie for booked seats."
        );

        return movieRepository.create(
                movie
        );
    }


    private Screen createAndSaveScreen() {

        Theatre theatre =
                new Theatre();

        theatre.setName(
                "Booked Seat Test Theatre"
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
                "Booked Seat Test Screen"
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
                LocalDate.of(
                        2026,
                        12,
                        1
                )
        );

        show.setShowTime(
                LocalTime.of(
                        18,
                        30
                )
        );

        show.setTicketPrice(
                250L
        );

        show.setAvailableSeats(
                200
        );

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
        private final ShowSeatAllocation allocation1;
        private final ShowSeatAllocation allocation2;

        private TestData(
                Movie movie,
                Screen screen,
                Show show,
                ShowSeatAllocation allocation1,
                ShowSeatAllocation allocation2
        ) {

            this.movie = movie;
            this.screen = screen;
            this.show = show;
            this.allocation1 = allocation1;
            this.allocation2 = allocation2;
        }
    }
}