package quickshow.dbms.project.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import quickshow.dbms.project.model.Theatre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TheatreRepositoryTest {

    @Autowired
    private TheatreRepository theatreRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void shouldCreateTheatre() {

        Theatre theatre =
                createCompleteTheatre();

        Theatre createdTheatre =
                theatreRepository.create(theatre);

        assertNotNull(createdTheatre);
        assertNotNull(
                createdTheatre.getTheatreId()
        );

        assertEquals(
                "PVR Cinemas",
                createdTheatre.getName()
        );

        assertEquals(
                "Mumbai",
                createdTheatre.getCity()
        );
    }


    // =========================================================
    // CREATE + READ
    // =========================================================

    @Test
    void shouldCreateAndRetrieveTheatreById() {

        Theatre theatre =
                createCompleteTheatre();

        Theatre createdTheatre =
                theatreRepository.create(theatre);

        Theatre retrievedTheatre =
                theatreRepository.findById(
                        createdTheatre.getTheatreId()
                );

        assertNotNull(retrievedTheatre);

        assertEquals(
                createdTheatre.getTheatreId(),
                retrievedTheatre.getTheatreId()
        );

        assertEquals(
                "PVR Cinemas",
                retrievedTheatre.getName()
        );

        assertEquals(
                "9876543210",
                retrievedTheatre.getContactNo()
        );

        assertEquals(
                "Phoenix Mall",
                retrievedTheatre.getBuildingName()
        );

        assertEquals(
                "Lower Parel Road",
                retrievedTheatre.getStreet()
        );

        assertEquals(
                "Lower Parel",
                retrievedTheatre.getArea()
        );

        assertEquals(
                "Mumbai",
                retrievedTheatre.getCity()
        );

        assertEquals(
                "Maharashtra",
                retrievedTheatre.getState()
        );

        assertEquals(
                "400013",
                retrievedTheatre.getPinCode()
        );
    }


    // =========================================================
    // FIND NON-EXISTENT THEATRE
    // =========================================================

    @Test
    void shouldReturnNullForNonExistentTheatre() {

        Theatre theatre =
                theatreRepository.findById(
                        Integer.MAX_VALUE
                );

        assertNull(theatre);
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void shouldFindAllTheatres() {

        Theatre theatre1 =
                createTheatre(
                        "Theatre Test A",
                        "Mumbai",
                        "Andheri"
                );

        Theatre theatre2 =
                createTheatre(
                        "Theatre Test B",
                        "Delhi",
                        "Saket"
                );

        Theatre savedTheatre1 =
                theatreRepository.create(theatre1);

        Theatre savedTheatre2 =
                theatreRepository.create(theatre2);

        List<Theatre> theatres =
                theatreRepository.findAll();

        assertTrue(
                theatres.stream()
                        .anyMatch(theatre ->
                                theatre.getTheatreId()
                                        .equals(
                                                savedTheatre1
                                                        .getTheatreId()
                                        )
                        )
        );

        assertTrue(
                theatres.stream()
                        .anyMatch(theatre ->
                                theatre.getTheatreId()
                                        .equals(
                                                savedTheatre2
                                                        .getTheatreId()
                                        )
                        )
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void shouldUpdateTheatre() {

        Theatre theatre =
                theatreRepository.create(
                        createCompleteTheatre()
                );

        theatre.setName(
                "PVR Cinemas Updated"
        );

        theatre.setCity("Pune");
        theatre.setArea("Hinjewadi");
        theatre.setContactNo("9123456789");

        int rowsUpdated =
                theatreRepository.update(theatre);

        assertEquals(1, rowsUpdated);

        Theatre updatedTheatre =
                theatreRepository.findById(
                        theatre.getTheatreId()
                );

        assertNotNull(updatedTheatre);

        assertEquals(
                "PVR Cinemas Updated",
                updatedTheatre.getName()
        );

        assertEquals(
                "Pune",
                updatedTheatre.getCity()
        );

        assertEquals(
                "Hinjewadi",
                updatedTheatre.getArea()
        );

        assertEquals(
                "9123456789",
                updatedTheatre.getContactNo()
        );
    }


    // =========================================================
    // UPDATE NON-EXISTENT THEATRE
    // =========================================================

    @Test
    void shouldUpdateZeroRowsForNonExistentTheatre() {

        Theatre theatre =
                createCompleteTheatre();

        theatre.setTheatreId(
                Integer.MAX_VALUE
        );

        int rowsUpdated =
                theatreRepository.update(theatre);

        assertEquals(0, rowsUpdated);
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void shouldDeleteTheatre() {

        Theatre theatre =
                theatreRepository.create(
                        createCompleteTheatre()
                );

        Integer theatreId =
                theatre.getTheatreId();

        assertTrue(
                theatreRepository.existsById(theatreId)
        );

        int rowsDeleted =
                theatreRepository.deleteById(
                        theatreId
                );

        assertEquals(1, rowsDeleted);

        assertFalse(
                theatreRepository.existsById(theatreId)
        );

        assertNull(
                theatreRepository.findById(theatreId)
        );
    }


    // =========================================================
    // DELETE NON-EXISTENT THEATRE
    // =========================================================

    @Test
    void shouldDeleteZeroRowsForNonExistentTheatre() {

        int rowsDeleted =
                theatreRepository.deleteById(
                        Integer.MAX_VALUE
                );

        assertEquals(0, rowsDeleted);
    }


    // =========================================================
    // EXISTS
    // =========================================================

    @Test
    void shouldCheckTheatreExistence() {

        Theatre theatre =
                theatreRepository.create(
                        createCompleteTheatre()
                );

        Integer theatreId =
                theatre.getTheatreId();

        assertTrue(
                theatreRepository.existsById(theatreId)
        );

        assertFalse(
                theatreRepository.existsById(
                        Integer.MAX_VALUE
                )
        );
    }


    // =========================================================
    // NULLABLE FIELDS
    // =========================================================

    @Test
    void shouldCreateTheatreWithNullableFields() {

        Theatre theatre = new Theatre();

        theatre.setName("Minimal Theatre");

        Theatre createdTheatre =
                theatreRepository.create(theatre);

        Theatre retrievedTheatre =
                theatreRepository.findById(
                        createdTheatre.getTheatreId()
                );

        assertNotNull(retrievedTheatre);

        assertEquals(
                "Minimal Theatre",
                retrievedTheatre.getName()
        );

        assertNull(
                retrievedTheatre.getContactNo()
        );

        assertNull(
                retrievedTheatre.getBuildingName()
        );

        assertNull(
                retrievedTheatre.getStreet()
        );

        assertNull(
                retrievedTheatre.getArea()
        );

        assertNull(
                retrievedTheatre.getCity()
        );

        assertNull(
                retrievedTheatre.getState()
        );

        assertNull(
                retrievedTheatre.getPinCode()
        );
    }


    // =========================================================
    // FIND BY CITY
    // =========================================================

    @Test
    void shouldFindTheatresByCity() {

        Theatre mumbaiTheatre =
                createTheatre(
                        "Mumbai Theatre Test",
                        "Mumbai",
                        "Andheri"
                );

        Theatre delhiTheatre =
                createTheatre(
                        "Delhi Theatre Test",
                        "Delhi",
                        "Saket"
                );

        Theatre savedMumbai =
                theatreRepository.create(
                        mumbaiTheatre
                );

        Theatre savedDelhi =
                theatreRepository.create(
                        delhiTheatre
                );

        List<Theatre> results =
                theatreRepository.findByCity(
                        "Mumbai"
                );

        assertTrue(
                results.stream()
                        .anyMatch(theatre ->
                                theatre.getTheatreId()
                                        .equals(
                                                savedMumbai
                                                        .getTheatreId()
                                        )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(theatre ->
                                theatre.getTheatreId()
                                        .equals(
                                                savedDelhi
                                                        .getTheatreId()
                                        )
                        )
        );
    }


    // =========================================================
    // FIND BY CITY + AREA
    // =========================================================

    @Test
    void shouldFindTheatresByCityAndArea() {

        Theatre theatre1 =
                createTheatre(
                        "Andheri Theatre 1",
                        "Mumbai",
                        "Andheri"
                );

        Theatre theatre2 =
                createTheatre(
                        "Andheri Theatre 2",
                        "Mumbai",
                        "Andheri"
                );

        Theatre theatre3 =
                createTheatre(
                        "Saket Theatre",
                        "Delhi",
                        "Saket"
                );

        Theatre savedTheatre1 =
                theatreRepository.create(theatre1);

        Theatre savedTheatre2 =
                theatreRepository.create(theatre2);

        Theatre savedTheatre3 =
                theatreRepository.create(theatre3);

        List<Theatre> results =
                theatreRepository.findByCityAndArea(
                        "Mumbai",
                        "Andheri"
                );

        assertTrue(
                results.stream()
                        .anyMatch(theatre ->
                                theatre.getTheatreId()
                                        .equals(
                                                savedTheatre1
                                                        .getTheatreId()
                                        )
                        )
        );

        assertTrue(
                results.stream()
                        .anyMatch(theatre ->
                                theatre.getTheatreId()
                                        .equals(
                                                savedTheatre2
                                                        .getTheatreId()
                                        )
                        )
        );

        assertFalse(
                results.stream()
                        .anyMatch(theatre ->
                                theatre.getTheatreId()
                                        .equals(
                                                savedTheatre3
                                                        .getTheatreId()
                                        )
                        )
        );
    }


    // =========================================================
    // NOT NULL CONSTRAINT
    // =========================================================

    @Test
    void shouldRejectTheatreWithoutName() {

        Theatre theatre =
                createCompleteTheatre();

        theatre.setName(null);

        assertThrows(
                Exception.class,
                () -> theatreRepository.create(theatre)
        );
    }


    // =========================================================
    // HELPER METHODS
    // =========================================================

    private Theatre createCompleteTheatre() {

        Theatre theatre = new Theatre();

        theatre.setName("PVR Cinemas");
        theatre.setContactNo("9876543210");
        theatre.setBuildingName("Phoenix Mall");
        theatre.setStreet("Lower Parel Road");
        theatre.setArea("Lower Parel");
        theatre.setCity("Mumbai");
        theatre.setState("Maharashtra");
        theatre.setPinCode("400013");

        return theatre;
    }


    private Theatre createTheatre(
            String name,
            String city,
            String area
    ) {

        Theatre theatre =
                createCompleteTheatre();

        theatre.setName(name);
        theatre.setCity(city);
        theatre.setArea(area);

        return theatre;
    }
}