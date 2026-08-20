package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import quickshow.dbms.project.model.Theatre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TheatreRepositoryTest {

    @Autowired
    private TheatreRepository theatreRepository;


    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldSaveAndRetrieveTheatre() {

        Theatre theatre = createCompleteTheatre();

        Theatre savedTheatre = theatreRepository.save(theatre);

        assertNotNull(savedTheatre.getTheatreId());

        Theatre retrievedTheatre =
                theatreRepository.findById(savedTheatre.getTheatreId())
                        .orElseThrow();

        assertEquals("PVR Phoenix", retrievedTheatre.getName());
        assertEquals("9876543210", retrievedTheatre.getContactNo());
        assertEquals("Phoenix Mall", retrievedTheatre.getBuildingName());
        assertEquals("High Street", retrievedTheatre.getStreet());
        assertEquals("Lower Parel", retrievedTheatre.getArea());
        assertEquals("Mumbai", retrievedTheatre.getCity());
        assertEquals("Maharashtra", retrievedTheatre.getState());
        assertEquals("400013", retrievedTheatre.getPinCode());
    }


    // =========================================================
    // FIND BY ID
    // =========================================================

    @Test
    void shouldFindTheatreById() {

        Theatre theatre = createCompleteTheatre();

        Theatre savedTheatre = theatreRepository.save(theatre);

        Integer theatreId = savedTheatre.getTheatreId();

        assertTrue(theatreRepository.existsById(theatreId));

        Theatre retrievedTheatre =
                theatreRepository.findById(theatreId)
                        .orElseThrow();

        assertEquals(theatreId, retrievedTheatre.getTheatreId());
        assertEquals("PVR Phoenix", retrievedTheatre.getName());
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindSavedTheatres() {

        Theatre theatre1 = createTheatre("PVR Phoenix", "Mumbai");
        Theatre theatre2 = createTheatre("INOX R-City", "Mumbai");

        Theatre savedTheatre1 = theatreRepository.save(theatre1);
        Theatre savedTheatre2 = theatreRepository.save(theatre2);

        List<Theatre> theatres = theatreRepository.findAll();

        assertTrue(
                theatres.stream()
                        .anyMatch(t ->
                                t.getTheatreId()
                                        .equals(savedTheatre1.getTheatreId())
                        )
        );

        assertTrue(
                theatres.stream()
                        .anyMatch(t ->
                                t.getTheatreId()
                                        .equals(savedTheatre2.getTheatreId())
                        )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateTheatre() {

        Theatre theatre = createCompleteTheatre();

        Theatre savedTheatre = theatreRepository.save(theatre);

        Integer theatreId = savedTheatre.getTheatreId();

        savedTheatre.setName("Updated PVR Phoenix");
        savedTheatre.setContactNo("9123456789");
        savedTheatre.setCity("Pune");

        theatreRepository.saveAndFlush(savedTheatre);

        Theatre updatedTheatre =
                theatreRepository.findById(theatreId)
                        .orElseThrow();

        assertEquals("Updated PVR Phoenix", updatedTheatre.getName());
        assertEquals("9123456789", updatedTheatre.getContactNo());
        assertEquals("Pune", updatedTheatre.getCity());

        // Verify fields that were not changed
        assertEquals("Phoenix Mall", updatedTheatre.getBuildingName());
        assertEquals("High Street", updatedTheatre.getStreet());
        assertEquals("Lower Parel", updatedTheatre.getArea());
        assertEquals("Maharashtra", updatedTheatre.getState());
        assertEquals("400013", updatedTheatre.getPinCode());
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteTheatre() {

        Theatre theatre = createCompleteTheatre();

        Theatre savedTheatre = theatreRepository.save(theatre);

        Integer theatreId = savedTheatre.getTheatreId();

        assertTrue(theatreRepository.existsById(theatreId));

        theatreRepository.deleteById(theatreId);

        assertFalse(theatreRepository.existsById(theatreId));

        assertTrue(
                theatreRepository.findById(theatreId).isEmpty()
        );
    }


    // =========================================================
    // NULLABLE FIELDS
    // =========================================================

    @Test
    void shouldSaveTheatreWithNullableFields() {

        Theatre theatre = new Theatre();

        theatre.setName("Minimal Theatre");

        Theatre savedTheatre = theatreRepository.save(theatre);

        Theatre retrievedTheatre =
                theatreRepository.findById(savedTheatre.getTheatreId())
                        .orElseThrow();

        assertEquals("Minimal Theatre", retrievedTheatre.getName());

        assertNull(retrievedTheatre.getContactNo());
        assertNull(retrievedTheatre.getBuildingName());
        assertNull(retrievedTheatre.getStreet());
        assertNull(retrievedTheatre.getArea());
        assertNull(retrievedTheatre.getCity());
        assertNull(retrievedTheatre.getState());
        assertNull(retrievedTheatre.getPinCode());
    }


    // =========================================================
    // NOT NULL CONSTRAINT
    // =========================================================

    @Test
    void shouldRejectTheatreWithoutName() {

        Theatre theatre = new Theatre();

        theatre.setContactNo("9876543210");
        theatre.setCity("Mumbai");

        assertThrows(
                Exception.class,
                () -> theatreRepository.saveAndFlush(theatre)
        );
    }


    // =========================================================
    // NAME LENGTH
    // =========================================================

    @Test
    void shouldRejectNameExceedingMaximumLength() {

        Theatre theatre = new Theatre();

        theatre.setName(
                "This theatre name is deliberately made longer than fifty characters"
        );

        assertThrows(
                Exception.class,
                () -> theatreRepository.saveAndFlush(theatre)
        );
    }


    // =========================================================
    // CONTACT NUMBER LENGTH
    // =========================================================

    @Test
    void shouldRejectContactNumberExceedingMaximumLength() {

        Theatre theatre = new Theatre();

        theatre.setName("Contact Length Test");
        theatre.setContactNo("12345678901");

        assertThrows(
                Exception.class,
                () -> theatreRepository.saveAndFlush(theatre)
        );
    }


    // =========================================================
    // BUILDING NAME LENGTH
    // =========================================================

    @Test
    void shouldRejectBuildingNameExceedingMaximumLength() {

        Theatre theatre = new Theatre();

        theatre.setName("Building Length Test");
        theatre.setBuildingName(
                "This building name is deliberately longer than thirty characters"
        );

        assertThrows(
                Exception.class,
                () -> theatreRepository.saveAndFlush(theatre)
        );
    }


    // =========================================================
    // STREET LENGTH
    // =========================================================

    @Test
    void shouldRejectStreetExceedingMaximumLength() {

        Theatre theatre = new Theatre();

        theatre.setName("Street Length Test");
        theatre.setStreet(
                "This street name is deliberately longer"
        );

        assertThrows(
                Exception.class,
                () -> theatreRepository.saveAndFlush(theatre)
        );
    }


    // =========================================================
    // AREA LENGTH
    // =========================================================

    @Test
    void shouldRejectAreaExceedingMaximumLength() {

        Theatre theatre = new Theatre();

        theatre.setName("Area Length Test");
        theatre.setArea(
                "This area name is deliberately longer"
        );

        assertThrows(
                Exception.class,
                () -> theatreRepository.saveAndFlush(theatre)
        );
    }


    // =========================================================
    // CITY LENGTH
    // =========================================================

    @Test
    void shouldRejectCityExceedingMaximumLength() {

        Theatre theatre = new Theatre();

        theatre.setName("City Length Test");
        theatre.setCity(
                "This city name is deliberately longer"
        );

        assertThrows(
                Exception.class,
                () -> theatreRepository.saveAndFlush(theatre)
        );
    }


    // =========================================================
    // STATE LENGTH
    // =========================================================

    @Test
    void shouldRejectStateExceedingMaximumLength() {

        Theatre theatre = new Theatre();

        theatre.setName("State Length Test");
        theatre.setState(
                "This state name is deliberately longer"
        );

        assertThrows(
                Exception.class,
                () -> theatreRepository.saveAndFlush(theatre)
        );
    }


    // =========================================================
    // PIN CODE LENGTH
    // =========================================================

    @Test
    void shouldRejectPinCodeExceedingMaximumLength() {

        Theatre theatre = new Theatre();

        theatre.setName("Pin Code Length Test");
        theatre.setPinCode("12345678901");

        assertThrows(
                Exception.class,
                () -> theatreRepository.saveAndFlush(theatre)
        );
    }


    // =========================================================
    // HELPER METHODS
    // =========================================================

    private Theatre createCompleteTheatre() {

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


    private Theatre createTheatre(String name, String city) {

        Theatre theatre = new Theatre();

        theatre.setName(name);
        theatre.setCity(city);

        return theatre;
    }
}