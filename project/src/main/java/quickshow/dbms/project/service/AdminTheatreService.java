package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;

import quickshow.dbms.project.dto.TheatreListDTO;
import quickshow.dbms.project.repository.AdminTheatreRepository;

import java.util.List;

@Service
public class AdminTheatreService {

    private final AdminTheatreRepository adminTheatreRepository;

    public AdminTheatreService(
            AdminTheatreRepository adminTheatreRepository
    ) {
        this.adminTheatreRepository =
                adminTheatreRepository;
    }


    // =========================================================
    // GET ALL
    // =========================================================

    public List<TheatreListDTO> getAllTheatres() {

        return adminTheatreRepository.findAll();
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    public TheatreListDTO getTheatreById(
            Integer theatreId
    ) {

        return adminTheatreRepository.findById(
                theatreId
        );
    }


    // =========================================================
    // CREATE
    // =========================================================

    public TheatreListDTO createTheatre(
            TheatreListDTO theatre
    ) {

        Integer theatreId =
                adminTheatreRepository.create(theatre);

        /*
         * TheatreListDTO has @AllArgsConstructor but
         * no setters shown in the uploaded file.
         *
         * Therefore create a new DTO containing
         * the generated ID.
         */

        return new TheatreListDTO(
                theatreId,
                theatre.getName(),
                theatre.getBuildingName(),
                theatre.getStreet(),
                theatre.getArea(),
                theatre.getCity(),
                theatre.getState(),
                theatre.getPinCode()
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    public TheatreListDTO updateTheatre(
            Integer theatreId,
            TheatreListDTO theatre
    ) {

        if (!adminTheatreRepository.existsById(
                theatreId
        )) {
            return null;
        }

        adminTheatreRepository.update(
                theatreId,
                theatre
        );

        return new TheatreListDTO(
                theatreId,
                theatre.getName(),
                theatre.getBuildingName(),
                theatre.getStreet(),
                theatre.getArea(),
                theatre.getCity(),
                theatre.getState(),
                theatre.getPinCode()
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    public String deleteTheatre(
            Integer theatreId
    ) {

        if (!adminTheatreRepository.existsById(
                theatreId
        )) {
            return "NOT_FOUND";
        }

        /*
         * Do not delete a theatre which still has screens.
         */

        if (adminTheatreRepository.hasScreens(
                theatreId
        )) {
            return "HAS_SCREENS";
        }

        int deleted =
                adminTheatreRepository.delete(
                        theatreId
                );

        if (deleted == 0) {
            return "NOT_FOUND";
        }

        return "DELETED";
    }
}