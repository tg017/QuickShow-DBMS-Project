package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import quickshow.dbms.project.model.Screen;
import quickshow.dbms.project.model.ScreenType;
import quickshow.dbms.project.model.Seat;
import quickshow.dbms.project.model.SeatId;
import quickshow.dbms.project.model.Theatre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SeatRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private TheatreRepository theatreRepository;


    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldSaveAndRetrieveSeat() {

        Screen screen = createAndSaveScreen();

        Seat seat = createSeat(screen, 1, "A", 1);

        Seat savedSeat = seatRepository.saveAndFlush(seat);

        assertNotNull(savedSeat.getId());

        assertEquals(
                screen.getScreenId(),
                savedSeat.getId().getScreenId()
        );

        assertEquals(
                1,
                savedSeat.getId().getSeatId()
        );

        SeatId seatId = new SeatId(
                screen.getScreenId(),
                1
        );

        Seat retrievedSeat =
                seatRepository.findById(seatId)
                        .orElseThrow();

        assertEquals(
                "A",
                retrievedSeat.getRowNo()
        );

        assertEquals(
                1,
                retrievedSeat.getSeatNo()
        );

        assertEquals(
                screen.getScreenId(),
                retrievedSeat.getScreen().getScreenId()
        );
    }


    // =========================================================
    // FIND BY COMPOSITE ID
    // =========================================================

    @Test
    void shouldFindSeatUsingCompositeId() {

        Screen screen = createAndSaveScreen();

        Seat seat = createSeat(screen, 10, "B", 5);

        seatRepository.saveAndFlush(seat);

        SeatId seatId = new SeatId(
                screen.getScreenId(),
                10
        );

        assertTrue(
                seatRepository.existsById(seatId)
        );

        Seat retrievedSeat =
                seatRepository.findById(seatId)
                        .orElseThrow();

        assertEquals(
                seatId,
                retrievedSeat.getId()
        );

        assertEquals(
                "B",
                retrievedSeat.getRowNo()
        );

        assertEquals(
                5,
                retrievedSeat.getSeatNo()
        );
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindSavedSeats() {

        Screen screen = createAndSaveScreen();

        Seat seat1 = createSeat(screen, 1, "A", 1);
        Seat seat2 = createSeat(screen, 2, "A", 2);
        Seat seat3 = createSeat(screen, 3, "A", 3);

        Seat savedSeat1 = seatRepository.save(seat1);
        Seat savedSeat2 = seatRepository.save(seat2);
        Seat savedSeat3 = seatRepository.save(seat3);

        seatRepository.flush();

        List<Seat> seats = seatRepository.findAll();

        assertTrue(
                seats.stream()
                        .anyMatch(seat ->
                                seat.getId().equals(savedSeat1.getId())
                        )
        );

        assertTrue(
                seats.stream()
                        .anyMatch(seat ->
                                seat.getId().equals(savedSeat2.getId())
                        )
        );

        assertTrue(
                seats.stream()
                        .anyMatch(seat ->
                                seat.getId().equals(savedSeat3.getId())
                        )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateSeat() {

        Screen screen = createAndSaveScreen();

        Seat seat = createSeat(screen, 20, "C", 10);

        Seat savedSeat =
                seatRepository.saveAndFlush(seat);

        SeatId seatId = savedSeat.getId();

        savedSeat.setRowNo("D");
        savedSeat.setSeatNo(15);

        seatRepository.saveAndFlush(savedSeat);

        Seat updatedSeat =
                seatRepository.findById(seatId)
                        .orElseThrow();

        assertEquals(
                "D",
                updatedSeat.getRowNo()
        );

        assertEquals(
                15,
                updatedSeat.getSeatNo()
        );

        // Composite primary key must remain unchanged.
        assertEquals(
                seatId,
                updatedSeat.getId()
        );

        assertEquals(
                screen.getScreenId(),
                updatedSeat.getScreen().getScreenId()
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteSeat() {

        Screen screen = createAndSaveScreen();

        Seat seat = createSeat(screen, 30, "E", 20);

        Seat savedSeat =
                seatRepository.saveAndFlush(seat);

        SeatId seatId = savedSeat.getId();

        assertTrue(
                seatRepository.existsById(seatId)
        );

        seatRepository.deleteById(seatId);

        assertFalse(
                seatRepository.existsById(seatId)
        );

        assertTrue(
                seatRepository.findById(seatId).isEmpty()
        );
    }


    // =========================================================
    // SAME SEAT ID ON DIFFERENT SCREENS
    // =========================================================

    @Test
    void shouldAllowSameSeatIdOnDifferentScreens() {

        Screen screen1 = createAndSaveScreen("Screen 1");
        Screen screen2 = createAndSaveScreen("Screen 2");

        Seat seat1 = createSeat(screen1, 1, "A", 1);
        Seat seat2 = createSeat(screen2, 1, "A", 1);

        Seat savedSeat1 =
                seatRepository.saveAndFlush(seat1);

        Seat savedSeat2 =
                seatRepository.saveAndFlush(seat2);

        assertNotEquals(
                savedSeat1.getId(),
                savedSeat2.getId()
        );

        assertEquals(
                1,
                savedSeat1.getId().getSeatId()
        );

        assertEquals(
                1,
                savedSeat2.getId().getSeatId()
        );

        assertNotEquals(
                savedSeat1.getId().getScreenId(),
                savedSeat2.getId().getScreenId()
        );

        assertTrue(
                seatRepository.existsById(savedSeat1.getId())
        );

        assertTrue(
                seatRepository.existsById(savedSeat2.getId())
        );
    }


    // =========================================================
    // DUPLICATE COMPOSITE KEY
    // =========================================================

    @Test
    void shouldUpdateExistingSeatWhenCompositeKeyMatches() {

        Screen screen = createAndSaveScreen();

        Seat seat1 = createSeat(screen, 100, "F", 1);

        seatRepository.saveAndFlush(seat1);

        Seat seat2 = createSeat(screen, 100, "F", 2);

        Seat savedSeat =
                seatRepository.saveAndFlush(seat2);

        SeatId seatId =
                new SeatId(
                        screen.getScreenId(),
                        100
                );

        Seat retrievedSeat =
                seatRepository.findById(seatId)
                        .orElseThrow();

        assertEquals(
                seatId,
                savedSeat.getId()
        );

        assertEquals(
                "F",
                retrievedSeat.getRowNo()
        );

        assertEquals(
                2,
                retrievedSeat.getSeatNo()
        );
    }


    // =========================================================
    // NULL ID
    // =========================================================

    @Test
    void shouldRejectSeatWithoutId() {

        Screen screen = createAndSaveScreen();

        Seat seat = new Seat();

        seat.setId(null);
        seat.setScreen(screen);
        seat.setRowNo("A");
        seat.setSeatNo(1);

        assertThrows(
                Exception.class,
                () -> seatRepository.saveAndFlush(seat)
        );
    }


    // =========================================================
    // NULL SCREEN
    // =========================================================

    @Test
    void shouldRejectSeatWithoutScreen() {

        Seat seat = new Seat();

        SeatId seatId = new SeatId(
                999999,
                1
        );

        seat.setId(seatId);
        seat.setRowNo("A");
        seat.setSeatNo(1);
        seat.setScreen(null);

        assertThrows(
                Exception.class,
                () -> seatRepository.saveAndFlush(seat)
        );
    }


    // =========================================================
    // NULL ROW
    // =========================================================

    @Test
    void shouldRejectSeatWithoutRowNo() {

        Screen screen = createAndSaveScreen();

        Seat seat = createSeat(
                screen,
                200,
                null,
                1
        );

        assertThrows(
                Exception.class,
                () -> seatRepository.saveAndFlush(seat)
        );
    }


    // =========================================================
    // NULL SEAT NUMBER
    // =========================================================

    @Test
    void shouldRejectSeatWithoutSeatNo() {

        Screen screen = createAndSaveScreen();

        Seat seat = createSeat(
                screen,
                201,
                "A",
                null
        );

        assertThrows(
                Exception.class,
                () -> seatRepository.saveAndFlush(seat)
        );
    }


    // =========================================================
    // INVALID SCREEN FOREIGN KEY
    // =========================================================

    @Test
    void shouldRejectSeatWithNonExistentScreen() {

        Integer nonExistentScreenId =
                Integer.MAX_VALUE;

        SeatId seatId = new SeatId(
                nonExistentScreenId,
                1
        );

        Seat seat = new Seat();

        seat.setId(seatId);
        seat.setRowNo("A");
        seat.setSeatNo(1);

        Screen fakeScreen = new Screen();
        fakeScreen.setScreenId(nonExistentScreenId);

        seat.setScreen(fakeScreen);

        assertThrows(
                Exception.class,
                () -> seatRepository.saveAndFlush(seat)
        );
    }


    // =========================================================
    // @MapsId CONSISTENCY
    // =========================================================

    @Test
    void shouldMapScreenIdIntoCompositeKey() {

        Screen screen = createAndSaveScreen();

        Seat seat = new Seat();

        /*
         * Deliberately create the SeatId with a null screenId.
         * @MapsId("screenId") should derive the screenId
         * from the associated Screen.
         */
        seat.setId(
                new SeatId(null, 500)
        );

        seat.setScreen(screen);
        seat.setRowNo("G");
        seat.setSeatNo(1);

        Seat savedSeat =
                seatRepository.saveAndFlush(seat);

        assertNotNull(savedSeat.getId());

        assertEquals(
                screen.getScreenId(),
                savedSeat.getId().getScreenId()
        );

        assertEquals(
                500,
                savedSeat.getId().getSeatId()
        );
    }


    // =========================================================
    // ROW NUMBER AS STRING
    // =========================================================

    @Test
    void shouldPersistStringRowNumber() {

        Screen screen = createAndSaveScreen();

        Seat seat = createSeat(
                screen,
                600,
                "AA",
                1
        );

        Seat savedSeat =
                seatRepository.saveAndFlush(seat);

        Seat retrievedSeat =
                seatRepository.findById(
                        savedSeat.getId()
                ).orElseThrow();

        assertEquals(
                "AA",
                retrievedSeat.getRowNo()
        );
    }


    // =========================================================
    // SEAT NUMBER
    // =========================================================

    @Test
    void shouldPersistSeatNumberCorrectly() {

        Screen screen = createAndSaveScreen();

        Seat seat = createSeat(
                screen,
                700,
                "H",
                999
        );

        Seat savedSeat =
                seatRepository.saveAndFlush(seat);

        Seat retrievedSeat =
                seatRepository.findById(
                        savedSeat.getId()
                ).orElseThrow();

        assertEquals(
                999,
                retrievedSeat.getSeatNo()
        );
    }


    // =========================================================
    // HELPER METHODS
    // =========================================================

    private Screen createAndSaveScreen() {
        return createAndSaveScreen("Screen 1");
    }


    private Screen createAndSaveScreen(String screenName) {

        Theatre theatre = new Theatre();

        theatre.setName(
                "Test Theatre " + System.nanoTime()
        );

        theatre.setContactNo("9876543210");
        theatre.setBuildingName("Phoenix Mall");
        theatre.setStreet("High Street");
        theatre.setArea("Lower Parel");
        theatre.setCity("Mumbai");
        theatre.setState("Maharashtra");
        theatre.setPinCode("400013");

        Theatre savedTheatre =
                theatreRepository.saveAndFlush(theatre);

        Screen screen = new Screen();

        screen.setName(screenName);
        screen.setScreenType(ScreenType.TWO_D);
        screen.setCapacity(200);
        screen.setTheatre(savedTheatre);

        return screenRepository.saveAndFlush(screen);
    }


    private Seat createSeat(
            Screen screen,
            Integer seatId,
            String rowNo,
            Integer seatNo
    ) {

        Seat seat = new Seat();

        seat.setId(
                new SeatId(
                        screen.getScreenId(),
                        seatId
                )
        );

        seat.setScreen(screen);
        seat.setRowNo(rowNo);
        seat.setSeatNo(seatNo);

        return seat;
    }
}