package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import quickshow.dbms.project.model.Screen;
import quickshow.dbms.project.model.ScreenType;
import quickshow.dbms.project.model.Theatre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ScreenRepositoryTest {

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private TheatreRepository theatreRepository;


    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldSaveAndRetrieveScreen() {

        Theatre theatre = theatreRepository.save(
                createTheatre()
        );

        Screen screen = createCompleteScreen(theatre);

        Screen savedScreen = screenRepository.save(screen);

        assertNotNull(savedScreen.getScreenId());

        Screen retrievedScreen =
                screenRepository.findById(savedScreen.getScreenId())
                        .orElseThrow();

        assertEquals(
                "Screen 1",
                retrievedScreen.getName()
        );

        assertEquals(
                ScreenType.TWO_D,
                retrievedScreen.getScreenType()
        );

        assertEquals(
                200,
                retrievedScreen.getCapacity()
        );

        assertEquals(
                theatre.getTheatreId(),
                retrievedScreen.getTheatre().getTheatreId()
        );
    }


    // =========================================================
    // FIND BY ID
    // =========================================================

    @Test
    void shouldFindScreenById() {

        Theatre theatre = theatreRepository.save(
                createTheatre()
        );

        Screen screen = screenRepository.save(
                createCompleteScreen(theatre)
        );

        Integer screenId = screen.getScreenId();

        assertTrue(
                screenRepository.existsById(screenId)
        );

        Screen retrievedScreen =
                screenRepository.findById(screenId)
                        .orElseThrow();

        assertEquals(
                screenId,
                retrievedScreen.getScreenId()
        );
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindSavedScreens() {

        Theatre theatre = theatreRepository.save(
                createTheatre()
        );

        Screen screen1 = createCompleteScreen(theatre);
        screen1.setName("Screen 1");

        Screen screen2 = createCompleteScreen(theatre);
        screen2.setName("Screen 2");

        Screen savedScreen1 = screenRepository.save(screen1);
        Screen savedScreen2 = screenRepository.save(screen2);

        List<Screen> screens = screenRepository.findAll();

        assertTrue(
                screens.stream()
                        .anyMatch(screen ->
                                screen.getScreenId()
                                        .equals(savedScreen1.getScreenId())
                        )
        );

        assertTrue(
                screens.stream()
                        .anyMatch(screen ->
                                screen.getScreenId()
                                        .equals(savedScreen2.getScreenId())
                        )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateScreen() {

        Theatre theatre = theatreRepository.save(
                createTheatre()
        );

        Screen screen = screenRepository.save(
                createCompleteScreen(theatre)
        );

        Integer screenId = screen.getScreenId();

        screen.setName("Updated Screen");
        screen.setScreenType(ScreenType.IMAX);
        screen.setCapacity(350);

        screenRepository.saveAndFlush(screen);

        Screen updatedScreen =
                screenRepository.findById(screenId)
                        .orElseThrow();

        assertEquals(
                "Updated Screen",
                updatedScreen.getName()
        );

        assertEquals(
                ScreenType.IMAX,
                updatedScreen.getScreenType()
        );

        assertEquals(
                350,
                updatedScreen.getCapacity()
        );

        assertEquals(
                theatre.getTheatreId(),
                updatedScreen.getTheatre().getTheatreId()
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteScreen() {

        Theatre theatre = theatreRepository.save(
                createTheatre()
        );

        Screen screen = screenRepository.save(
                createCompleteScreen(theatre)
        );

        Integer screenId = screen.getScreenId();

        assertTrue(
                screenRepository.existsById(screenId)
        );

        screenRepository.deleteById(screenId);

        assertFalse(
                screenRepository.existsById(screenId)
        );

        assertTrue(
                screenRepository.findById(screenId).isEmpty()
        );
    }


    // =========================================================
    // SCREEN TYPE CONVERTER
    // =========================================================

    @Test
    void shouldPersistAllScreenTypes() {

        Theatre theatre = theatreRepository.save(
                createTheatre()
        );

        for (ScreenType screenType : ScreenType.values()) {

            Screen screen = createCompleteScreen(theatre);

            screen.setName(
                    "Test " + screenType.name()
            );

            screen.setScreenType(screenType);

            Screen savedScreen =
                    screenRepository.saveAndFlush(screen);

            Screen retrievedScreen =
                    screenRepository.findById(
                            savedScreen.getScreenId()
                    ).orElseThrow();

            assertEquals(
                    screenType,
                    retrievedScreen.getScreenType()
            );
        }
    }


    // =========================================================
    // NULLABLE / NON-NULLABLE FIELDS
    // =========================================================

    @Test
    void shouldRejectScreenWithoutName() {

        Theatre theatre = theatreRepository.save(
                createTheatre()
        );

        Screen screen = new Screen();

        screen.setScreenType(ScreenType.TWO_D);
        screen.setCapacity(200);
        screen.setTheatre(theatre);

        assertThrows(
                Exception.class,
                () -> screenRepository.saveAndFlush(screen)
        );
    }


    @Test
    void shouldRejectScreenWithoutScreenType() {

        Theatre theatre = theatreRepository.save(
                createTheatre()
        );

        Screen screen = new Screen();

        screen.setName("No Type Screen");
        screen.setCapacity(200);
        screen.setTheatre(theatre);

        assertThrows(
                Exception.class,
                () -> screenRepository.saveAndFlush(screen)
        );
    }


    @Test
    void shouldRejectScreenWithoutCapacity() {

        Theatre theatre = theatreRepository.save(
                createTheatre()
        );

        Screen screen = new Screen();

        screen.setName("No Capacity Screen");
        screen.setScreenType(ScreenType.TWO_D);
        screen.setTheatre(theatre);

        assertThrows(
                Exception.class,
                () -> screenRepository.saveAndFlush(screen)
        );
    }


    @Test
    void shouldRejectScreenWithoutTheatre() {

        Screen screen = new Screen();

        screen.setName("No Theatre Screen");
        screen.setScreenType(ScreenType.TWO_D);
        screen.setCapacity(200);

        assertThrows(
                Exception.class,
                () -> screenRepository.saveAndFlush(screen)
        );
    }


    // =========================================================
    // SCREEN NAME LENGTH
    // =========================================================

    @Test
    void shouldRejectScreenNameExceedingMaximumLength() {

        Theatre theatre = theatreRepository.save(
                createTheatre()
        );

        Screen screen = new Screen();

        screen.setName(
                "This screen name is deliberately made longer than fifty characters"
        );

        screen.setScreenType(ScreenType.TWO_D);
        screen.setCapacity(200);
        screen.setTheatre(theatre);

        assertThrows(
                Exception.class,
                () -> screenRepository.saveAndFlush(screen)
        );
    }


    // =========================================================
    // FOREIGN KEY
    // =========================================================

    @Test
    void shouldRejectScreenWithNonExistentTheatre() {

        Screen screen = new Screen();

        Theatre fakeTheatre = new Theatre();

        // Deliberately use an ID which doesn't exist.
        fakeTheatre.setTheatreId(Integer.MAX_VALUE);

        screen.setName("Invalid Theatre Screen");
        screen.setScreenType(ScreenType.TWO_D);
        screen.setCapacity(200);
        screen.setTheatre(fakeTheatre);

        assertThrows(
                Exception.class,
                () -> screenRepository.saveAndFlush(screen)
        );
    }


    // =========================================================
    // CAPACITY
    // =========================================================

    @Test
    void shouldPersistCapacityCorrectly() {

        Theatre theatre = theatreRepository.save(
                createTheatre()
        );

        Screen screen = createCompleteScreen(theatre);

        screen.setCapacity(1);

        Screen savedScreen =
                screenRepository.saveAndFlush(screen);

        Screen retrievedScreen =
                screenRepository.findById(
                        savedScreen.getScreenId()
                ).orElseThrow();

        assertEquals(
                1,
                retrievedScreen.getCapacity()
        );
    }


    // =========================================================
    // HELPER METHODS
    // =========================================================

    private Theatre createTheatre() {

        Theatre theatre = new Theatre();

        theatre.setName("PVR Phoenix");
        theatre.setContactNo("9876543210");
        theatre.setBuildingName("Phoenix Mall");
        theatre.setStreet("High Street");
        theatre.setArea("Lower Parel");
        theatre.setCity("Mumbai");
        theatre.setState("Maharashtra");
        theatre.setPinCode("400013");

        return theatre;
    }


    private Screen createCompleteScreen(Theatre theatre) {

        Screen screen = new Screen();

        screen.setName("Screen 1");
        screen.setScreenType(ScreenType.TWO_D);
        screen.setCapacity(200);
        screen.setTheatre(theatre);

        return screen;
    }
}