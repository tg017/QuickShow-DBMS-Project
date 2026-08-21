package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickshow.dbms.project.model.Screen;
import quickshow.dbms.project.model.ScreenType;
import quickshow.dbms.project.model.Theatre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScreenRepositoryTest {

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private TheatreRepository theatreRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateScreen() {

        Theatre theatre =
                createAndSaveTheatre();

        Screen screen =
                createScreen(
                        "Screen 1",
                        ScreenType.TWO_D,
                        200
                );

        Screen createdScreen =
                screenRepository.create(
                        screen,
                        theatre.getTheatreId()
                );

        assertNotNull(createdScreen);

        assertNotNull(
                createdScreen.getScreenId()
        );

        assertEquals(
                "Screen 1",
                createdScreen.getName()
        );

        assertEquals(
                ScreenType.TWO_D,
                createdScreen.getScreenType()
        );

        assertEquals(
                200,
                createdScreen.getCapacity()
        );
    }


    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldCreateAndRetrieveScreenById() {

        Theatre theatre =
                createAndSaveTheatre();

        Screen screen =
                createScreen(
                        "IMAX Screen",
                        ScreenType.IMAX,
                        300
                );

        Screen createdScreen =
                screenRepository.create(
                        screen,
                        theatre.getTheatreId()
                );

        Screen retrievedScreen =
                screenRepository.findById(
                        createdScreen.getScreenId()
                );

        assertNotNull(retrievedScreen);

        assertEquals(
                createdScreen.getScreenId(),
                retrievedScreen.getScreenId()
        );

        assertEquals(
                "IMAX Screen",
                retrievedScreen.getName()
        );

        assertEquals(
                ScreenType.IMAX,
                retrievedScreen.getScreenType()
        );

        assertEquals(
                300,
                retrievedScreen.getCapacity()
        );
    }


    // =========================================================
    // FIND NON-EXISTENT SCREEN
    // =========================================================

    @Test
    void shouldReturnNullForNonExistentScreen() {

        Screen screen =
                screenRepository.findById(
                        Integer.MAX_VALUE
                );

        assertNull(screen);
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindAllScreens() {

        Theatre theatre =
                createAndSaveTheatre();

        Screen screen1 =
                createScreen(
                        "Screen A",
                        ScreenType.TWO_D,
                        150
                );

        Screen screen2 =
                createScreen(
                        "Screen B",
                        ScreenType.THREE_D,
                        200
                );

        Screen savedScreen1 =
                screenRepository.create(
                        screen1,
                        theatre.getTheatreId()
                );

        Screen savedScreen2 =
                screenRepository.create(
                        screen2,
                        theatre.getTheatreId()
                );

        List<Screen> screens =
                screenRepository.findAll();

        assertTrue(
                screens.stream()
                        .anyMatch(screen ->
                                screen.getScreenId()
                                        .equals(
                                                savedScreen1
                                                        .getScreenId()
                                        )
                        )
        );

        assertTrue(
                screens.stream()
                        .anyMatch(screen ->
                                screen.getScreenId()
                                        .equals(
                                                savedScreen2
                                                        .getScreenId()
                                        )
                        )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateScreen() {

        Theatre theatre =
                createAndSaveTheatre();

        Screen screen =
                screenRepository.create(
                        createScreen(
                                "Original Screen",
                                ScreenType.TWO_D,
                                150
                        ),
                        theatre.getTheatreId()
                );

        screen.setName("Updated Screen");
        screen.setScreenType(ScreenType.IMAX_3D);
        screen.setCapacity(350);

        int rowsUpdated =
                screenRepository.update(screen);

        assertEquals(1, rowsUpdated);

        Screen updatedScreen =
                screenRepository.findById(
                        screen.getScreenId()
                );

        assertNotNull(updatedScreen);

        assertEquals(
                "Updated Screen",
                updatedScreen.getName()
        );

        assertEquals(
                ScreenType.IMAX_3D,
                updatedScreen.getScreenType()
        );

        assertEquals(
                350,
                updatedScreen.getCapacity()
        );
    }


    // =========================================================
    // UPDATE NON-EXISTENT SCREEN
    // =========================================================

    @Test
    void shouldUpdateZeroRowsForNonExistentScreen() {

        Screen screen =
                createScreen(
                        "Non Existing Screen",
                        ScreenType.TWO_D,
                        100
                );

        screen.setScreenId(
                Integer.MAX_VALUE
        );

        int rowsUpdated =
                screenRepository.update(screen);

        assertEquals(0, rowsUpdated);
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteScreen() {

        Theatre theatre =
                createAndSaveTheatre();

        Screen screen =
                screenRepository.create(
                        createScreen(
                                "Delete Screen",
                                ScreenType.TWO_D,
                                100
                        ),
                        theatre.getTheatreId()
                );

        Integer screenId =
                screen.getScreenId();

        assertTrue(
                screenRepository.existsById(screenId)
        );

        int rowsDeleted =
                screenRepository.deleteById(screenId);

        assertEquals(1, rowsDeleted);

        assertFalse(
                screenRepository.existsById(screenId)
        );

        assertNull(
                screenRepository.findById(screenId)
        );
    }


    // =========================================================
    // DELETE NON-EXISTENT SCREEN
    // =========================================================

    @Test
    void shouldDeleteZeroRowsForNonExistentScreen() {

        int rowsDeleted =
                screenRepository.deleteById(
                        Integer.MAX_VALUE
                );

        assertEquals(0, rowsDeleted);
    }


    // =========================================================
    // EXISTS
    // =========================================================

    @Test
    void shouldCheckScreenExistence() {

        Theatre theatre =
                createAndSaveTheatre();

        Screen screen =
                screenRepository.create(
                        createScreen(
                                "Existence Screen",
                                ScreenType.TWO_D,
                                100
                        ),
                        theatre.getTheatreId()
                );

        Integer screenId =
                screen.getScreenId();

        assertTrue(
                screenRepository.existsById(screenId)
        );

        assertFalse(
                screenRepository.existsById(
                        Integer.MAX_VALUE
                )
        );
    }


    // =========================================================
    // FIND BY THEATRE
    // =========================================================

    @Test
    void shouldFindScreensByTheatre() {

        Theatre theatre1 =
                createAndSaveTheatre();

        Theatre theatre2 =
                createAndSaveTheatre();

        Screen screen1 =
                screenRepository.create(
                        createScreen(
                                "Theatre 1 Screen A",
                                ScreenType.TWO_D,
                                100
                        ),
                        theatre1.getTheatreId()
                );

        Screen screen2 =
                screenRepository.create(
                        createScreen(
                                "Theatre 1 Screen B",
                                ScreenType.IMAX,
                                250
                        ),
                        theatre1.getTheatreId()
                );

        Screen screen3 =
                screenRepository.create(
                        createScreen(
                                "Theatre 2 Screen",
                                ScreenType.THREE_D,
                                150
                        ),
                        theatre2.getTheatreId()
                );

        List<Screen> results =
                screenRepository.findByTheatre(
                        theatre1.getTheatreId()
                );

        assertTrue(
                results.stream()
                        .anyMatch(screen ->
                                screen.getScreenId()
                                        .equals(
                                                screen1
                                                        .getScreenId()
                                        )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(screen ->
                                screen.getScreenId()
                                        .equals(
                                                screen2
                                                        .getScreenId()
                                        )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(screen ->
                                screen.getScreenId()
                                        .equals(
                                                screen3
                                                        .getScreenId()
                                        )
                        )
        );
    }


    // =========================================================
    // FIND BY SCREEN TYPE
    // =========================================================

    @Test
    void shouldFindScreensByScreenType() {

        Theatre theatre =
                createAndSaveTheatre();

        Screen twoD =
                screenRepository.create(
                        createScreen(
                                "2D Screen",
                                ScreenType.TWO_D,
                                100
                        ),
                        theatre.getTheatreId()
                );

        Screen imax =
                screenRepository.create(
                        createScreen(
                                "IMAX Screen",
                                ScreenType.IMAX,
                                300
                        ),
                        theatre.getTheatreId()
                );

        List<Screen> results =
                screenRepository.findByScreenType(
                        ScreenType.IMAX
                );

        assertTrue(
                results.stream()
                        .anyMatch(screen ->
                                screen.getScreenId()
                                        .equals(
                                                imax.getScreenId()
                                        )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(screen ->
                                screen.getScreenId()
                                        .equals(
                                                twoD.getScreenId()
                                        )
                        )
        );
    }


    // =========================================================
    // FIND BY THEATRE + SCREEN TYPE
    // =========================================================

    @Test
    void shouldFindScreensByTheatreAndScreenType() {

        Theatre theatre1 =
                createAndSaveTheatre();

        Theatre theatre2 =
                createAndSaveTheatre();

        Screen matchingScreen =
                screenRepository.create(
                        createScreen(
                                "Matching IMAX",
                                ScreenType.IMAX,
                                300
                        ),
                        theatre1.getTheatreId()
                );

        Screen wrongType =
                screenRepository.create(
                        createScreen(
                                "Wrong Type",
                                ScreenType.TWO_D,
                                150
                        ),
                        theatre1.getTheatreId()
                );

        Screen wrongTheatre =
                screenRepository.create(
                        createScreen(
                                "Wrong Theatre",
                                ScreenType.IMAX,
                                300
                        ),
                        theatre2.getTheatreId()
                );

        List<Screen> results =
                screenRepository.findByTheatreAndScreenType(
                        theatre1.getTheatreId(),
                        ScreenType.IMAX
                );

        assertTrue(
                results.stream()
                        .anyMatch(screen ->
                                screen.getScreenId()
                                        .equals(
                                                matchingScreen
                                                        .getScreenId()
                                        )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(screen ->
                                screen.getScreenId()
                                        .equals(
                                                wrongType
                                                        .getScreenId()
                                        )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(screen ->
                                screen.getScreenId()
                                        .equals(
                                                wrongTheatre
                                                        .getScreenId()
                                        )
                        )
        );
    }


    // =========================================================
    // SCREEN TYPE VALUES
    // =========================================================

    @Test
    void shouldPersistEveryScreenType() {

        Theatre theatre =
                createAndSaveTheatre();

        for (ScreenType screenType :
                ScreenType.values()) {

            Screen screen =
                    screenRepository.create(
                            createScreen(
                                    "Test " +
                                            screenType.name(),
                                    screenType,
                                    100
                            ),
                            theatre.getTheatreId()
                    );

            Screen retrieved =
                    screenRepository.findById(
                            screen.getScreenId()
                    );

            assertNotNull(retrieved);

            assertEquals(
                    screenType,
                    retrieved.getScreenType()
            );
        }
    }


    // =========================================================
    // ZERO CAPACITY
    // =========================================================

    @Test
    void shouldAllowZeroSeatingCapacity() {

        Theatre theatre =
                createAndSaveTheatre();

        Screen screen =
                createScreen(
                        "Zero Capacity",
                        ScreenType.TWO_D,
                        0
                );

        Screen created =
                screenRepository.create(
                        screen,
                        theatre.getTheatreId()
                );

        assertNotNull(created.getScreenId());

        Screen retrieved =
                screenRepository.findById(
                        created.getScreenId()
                );

        assertEquals(
                0,
                retrieved.getCapacity()
        );
    }


    // =========================================================
    // NEGATIVE CAPACITY
    // =========================================================

    @Test
    void shouldRejectNegativeSeatingCapacity() {

        Theatre theatre =
                createAndSaveTheatre();

        Screen screen =
                createScreen(
                        "Invalid Capacity",
                        ScreenType.TWO_D,
                        -1
                );

        assertThrows(
                Exception.class,
                () -> screenRepository.create(
                        screen,
                        theatre.getTheatreId()
                )
        );
    }


    // =========================================================
    // NULL SCREEN TYPE
    // =========================================================

    @Test
    void shouldRejectNullScreenType() {

        Theatre theatre =
                createAndSaveTheatre();

        Screen screen =
                createScreen(
                        "Null Type",
                        ScreenType.TWO_D,
                        100
                );

        screen.setScreenType(null);

        assertThrows(
                Exception.class,
                () -> screenRepository.create(
                        screen,
                        theatre.getTheatreId()
                )
        );
    }


    // =========================================================
    // NULL SCREEN NAME
    // =========================================================

    @Test
    void shouldRejectNullScreenName() {

        Theatre theatre =
                createAndSaveTheatre();

        Screen screen =
                createScreen(
                        "Valid Screen",
                        ScreenType.TWO_D,
                        100
                );

        screen.setName(null);

        assertThrows(
                Exception.class,
                () -> screenRepository.create(
                        screen,
                        theatre.getTheatreId()
                )
        );
    }


    // =========================================================
    // NULL CAPACITY
    // =========================================================

    @Test
    void shouldRejectNullCapacity() {

        Theatre theatre =
                createAndSaveTheatre();

        Screen screen =
                createScreen(
                        "Null Capacity",
                        ScreenType.TWO_D,
                        100
                );

        screen.setCapacity(null);

        assertThrows(
                Exception.class,
                () -> screenRepository.create(
                        screen,
                        theatre.getTheatreId()
                )
        );
    }


    // =========================================================
    // INVALID THEATRE FK
    // =========================================================

    @Test
    void shouldRejectNonExistentTheatre() {

        Screen screen =
                createScreen(
                        "Invalid Theatre",
                        ScreenType.TWO_D,
                        100
                );

        assertThrows(
                Exception.class,
                () -> screenRepository.create(
                        screen,
                        Integer.MAX_VALUE
                )
        );
    }


    // =========================================================
    // HELPERS
    // =========================================================

    private Theatre createAndSaveTheatre() {

        Theatre theatre = new Theatre();

        theatre.setName(
                "Test Theatre"
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

        return theatreRepository.create(
                theatre
        );
    }


    private Screen createScreen(
            String name,
            ScreenType screenType,
            Integer capacity
    ) {

        Screen screen = new Screen();

        screen.setName(name);
        screen.setScreenType(screenType);
        screen.setCapacity(capacity);

        return screen;
    }
}