package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickshow.dbms.project.model.Seat;
import quickshow.dbms.project.model.SeatId;
import quickshow.dbms.project.model.Screen;
import quickshow.dbms.project.model.ScreenType;
import quickshow.dbms.project.model.Theatre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SeatRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private TheatreRepository theatreRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateSeat() {

        Screen screen =
                createAndSaveScreen();

        Seat seat =
                createSeat(
                        screen,
                        1,
                        "A",
                        1
                );

        Seat createdSeat =
                seatRepository.create(seat);

        assertNotNull(createdSeat);
        assertNotNull(createdSeat.getId());

        assertEquals(
                screen.getScreenId(),
                createdSeat.getId().getScreenId()
        );

        assertEquals(
                1,
                createdSeat.getId().getSeatId()
        );

        assertEquals(
                "A",
                createdSeat.getRowNo()
        );

        assertEquals(
                1,
                createdSeat.getSeatNo()
        );
    }


    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldCreateAndRetrieveSeatByCompositeId() {

        Screen screen =
                createAndSaveScreen();

        Seat seat =
                createSeat(
                        screen,
                        10,
                        "B",
                        5
                );

        seatRepository.create(seat);

        SeatId id =
                new SeatId(
                        screen.getScreenId(),
                        10
                );

        Seat retrievedSeat =
                seatRepository.findById(id);

        assertNotNull(retrievedSeat);

        assertEquals(
                screen.getScreenId(),
                retrievedSeat.getId().getScreenId()
        );

        assertEquals(
                10,
                retrievedSeat.getId().getSeatId()
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
    // FIND NON-EXISTENT SEAT
    // =========================================================

    @Test
    void shouldReturnNullForNonExistentSeat() {

        SeatId id =
                new SeatId(
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                );

        Seat seat =
                seatRepository.findById(id);

        assertNull(seat);
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindAllSeats() {

        Screen screen =
                createAndSaveScreen();

        Seat seat1 =
                createSeat(
                        screen,
                        1,
                        "A",
                        1
                );

        Seat seat2 =
                createSeat(
                        screen,
                        2,
                        "A",
                        2
                );

        seatRepository.create(seat1);
        seatRepository.create(seat2);

        List<Seat> seats =
                seatRepository.findAll();

        assertTrue(
                seats.stream()
                        .anyMatch(seat ->
                                seat.getId().equals(
                                        seat1.getId()
                                )
                        )
        );

        assertTrue(
                seats.stream()
                        .anyMatch(seat ->
                                seat.getId().equals(
                                        seat2.getId()
                                )
                        )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateSeat() {

        Screen screen =
                createAndSaveScreen();

        Seat seat =
                createSeat(
                        screen,
                        1,
                        "A",
                        1
                );

        seatRepository.create(seat);

        seat.setRowNo("C");
        seat.setSeatNo(20);

        int rowsUpdated =
                seatRepository.update(seat);

        assertEquals(1, rowsUpdated);

        Seat updatedSeat =
                seatRepository.findById(
                        seat.getId()
                );

        assertNotNull(updatedSeat);

        assertEquals(
                "C",
                updatedSeat.getRowNo()
        );

        assertEquals(
                20,
                updatedSeat.getSeatNo()
        );
    }


    // =========================================================
    // UPDATE NON-EXISTENT SEAT
    // =========================================================

    @Test
    void shouldUpdateZeroRowsForNonExistentSeat() {

        Seat seat = new Seat();

        seat.setId(
                new SeatId(
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                )
        );

        seat.setRowNo("A");
        seat.setSeatNo(1);

        int rowsUpdated =
                seatRepository.update(seat);

        assertEquals(0, rowsUpdated);
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteSeat() {

        Screen screen =
                createAndSaveScreen();

        Seat seat =
                createSeat(
                        screen,
                        1,
                        "A",
                        1
                );

        seatRepository.create(seat);

        assertTrue(
                seatRepository.existsById(
                        seat.getId()
                )
        );

        int rowsDeleted =
                seatRepository.deleteById(
                        seat.getId()
                );

        assertEquals(1, rowsDeleted);

        assertFalse(
                seatRepository.existsById(
                        seat.getId()
                )
        );

        assertNull(
                seatRepository.findById(
                        seat.getId()
                )
        );
    }


    // =========================================================
    // DELETE NON-EXISTENT
    // =========================================================

    @Test
    void shouldDeleteZeroRowsForNonExistentSeat() {

        SeatId id =
                new SeatId(
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                );

        int rowsDeleted =
                seatRepository.deleteById(id);

        assertEquals(0, rowsDeleted);
    }


    // =========================================================
    // EXISTS
    // =========================================================

    @Test
    void shouldCheckSeatExistence() {

        Screen screen =
                createAndSaveScreen();

        Seat seat =
                createSeat(
                        screen,
                        1,
                        "A",
                        1
                );

        seatRepository.create(seat);

        assertTrue(
                seatRepository.existsById(
                        seat.getId()
                )
        );

        assertFalse(
                seatRepository.existsById(
                        new SeatId(
                                Integer.MAX_VALUE,
                                Integer.MAX_VALUE
                        )
                )
        );
    }


    // =========================================================
    // COMPOSITE PRIMARY KEY
    // =========================================================

    @Test
    void shouldRejectDuplicateCompositePrimaryKey() {

        Screen screen =
                createAndSaveScreen();

        Seat seat1 =
                createSeat(
                        screen,
                        100,
                        "A",
                        1
                );

        seatRepository.create(seat1);

        Seat seat2 =
                createSeat(
                        screen,
                        100,
                        "B",
                        2
                );

        assertThrows(
                Exception.class,
                () -> seatRepository.create(seat2)
        );
    }


    // =========================================================
    // SAME SEAT ID ON DIFFERENT SCREEN
    // =========================================================

    @Test
    void shouldAllowSameSeatIdOnDifferentScreens() {

        Screen screen1 =
                createAndSaveScreen();

        Screen screen2 =
                createAndSaveScreen();

        Seat seat1 =
                createSeat(
                        screen1,
                        1,
                        "A",
                        1
                );

        Seat seat2 =
                createSeat(
                        screen2,
                        1,
                        "A",
                        1
                );

        assertDoesNotThrow(
                () -> seatRepository.create(seat1)
        );

        assertDoesNotThrow(
                () -> seatRepository.create(seat2)
        );
    }


    // =========================================================
    // UNIQUE SCREEN + ROW + SEAT NUMBER
    // =========================================================

    @Test
    void shouldRejectDuplicatePhysicalSeatPosition() {

        Screen screen =
                createAndSaveScreen();

        Seat seat1 =
                createSeat(
                        screen,
                        1,
                        "A",
                        10
                );

        seatRepository.create(seat1);

        Seat seat2 =
                createSeat(
                        screen,
                        2,
                        "A",
                        10
                );

        assertThrows(
                Exception.class,
                () -> seatRepository.create(seat2)
        );
    }


    // =========================================================
    // SAME ROW AND SEAT NUMBER ON DIFFERENT SCREEN
    // =========================================================

    @Test
    void shouldAllowSamePhysicalSeatOnDifferentScreens() {

        Screen screen1 =
                createAndSaveScreen();

        Screen screen2 =
                createAndSaveScreen();

        Seat seat1 =
                createSeat(
                        screen1,
                        1,
                        "A",
                        10
                );

        Seat seat2 =
                createSeat(
                        screen2,
                        1,
                        "A",
                        10
                );

        assertDoesNotThrow(
                () -> seatRepository.create(seat1)
        );

        assertDoesNotThrow(
                () -> seatRepository.create(seat2)
        );
    }


    // =========================================================
    // FIND BY SCREEN
    // =========================================================

    @Test
    void shouldFindSeatsByScreen() {

        Screen screen1 =
                createAndSaveScreen();

        Screen screen2 =
                createAndSaveScreen();

        Seat seat1 =
                createSeat(
                        screen1,
                        1,
                        "A",
                        1
                );

        Seat seat2 =
                createSeat(
                        screen1,
                        2,
                        "A",
                        2
                );

        Seat seat3 =
                createSeat(
                        screen2,
                        1,
                        "A",
                        1
                );

        seatRepository.create(seat1);
        seatRepository.create(seat2);
        seatRepository.create(seat3);

        List<Seat> results =
                seatRepository.findByScreen(
                        screen1.getScreenId()
                );

        assertTrue(
                results.stream()
                        .anyMatch(seat ->
                                seat.getId().equals(
                                        seat1.getId()
                                )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(seat ->
                                seat.getId().equals(
                                        seat2.getId()
                                )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(seat ->
                                seat.getId().equals(
                                        seat3.getId()
                                )
                        )
        );
    }


    // =========================================================
    // FIND BY SCREEN AND ROW
    // =========================================================

    @Test
    void shouldFindSeatsByScreenAndRow() {

        Screen screen =
                createAndSaveScreen();

        Seat seatA1 =
                createSeat(
                        screen,
                        1,
                        "A",
                        1
                );

        Seat seatA2 =
                createSeat(
                        screen,
                        2,
                        "A",
                        2
                );

        Seat seatB1 =
                createSeat(
                        screen,
                        3,
                        "B",
                        1
                );

        seatRepository.create(seatA1);
        seatRepository.create(seatA2);
        seatRepository.create(seatB1);

        List<Seat> results =
                seatRepository.findByScreenAndRow(
                        screen.getScreenId(),
                        "A"
                );

        assertTrue(
                results.stream()
                        .anyMatch(seat ->
                                seat.getId().equals(
                                        seatA1.getId()
                                )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(seat ->
                                seat.getId().equals(
                                        seatA2.getId()
                                )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(seat ->
                                seat.getId().equals(
                                        seatB1.getId()
                                )
                        )
        );
    }


    // =========================================================
    // NULL ROW
    // =========================================================

    @Test
    void shouldRejectNullRow() {

        Screen screen =
                createAndSaveScreen();

        Seat seat =
                createSeat(
                        screen,
                        1,
                        null,
                        1
                );

        assertThrows(
                Exception.class,
                () -> seatRepository.create(seat)
        );
    }


    // =========================================================
    // NULL SEAT NUMBER
    // =========================================================

    @Test
    void shouldRejectNullSeatNumber() {

        Screen screen =
                createAndSaveScreen();

        Seat seat =
                createSeat(
                        screen,
                        1,
                        "A",
                        null
                );

        assertThrows(
                Exception.class,
                () -> seatRepository.create(seat)
        );
    }


    // =========================================================
    // INVALID SCREEN FK
    // =========================================================

    @Test
    void shouldRejectSeatForNonExistentScreen() {

        Seat seat = new Seat();

        seat.setId(
                new SeatId(
                        Integer.MAX_VALUE,
                        1
                )
        );

        seat.setRowNo("A");
        seat.setSeatNo(1);

        assertThrows(
                Exception.class,
                () -> seatRepository.create(seat)
        );
    }


    // =========================================================
    // HELPER: CREATE SCREEN
    // =========================================================

    private Screen createAndSaveScreen() {

        Theatre theatre =
                new Theatre();

        theatre.setName(
                "Seat Test Theatre"
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
                "Seat Test Screen"
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


    // =========================================================
    // HELPER: CREATE SEAT
    // =========================================================

    private Seat createSeat(
            Screen screen,
            Integer seatId,
            String rowNo,
            Integer seatNo
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
        seat.setRowNo(rowNo);
        seat.setSeatNo(seatNo);

        return seat;
    }
}