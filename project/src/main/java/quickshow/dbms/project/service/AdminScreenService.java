package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;

import quickshow.dbms.project.dto.AdminScreenDTO;
import quickshow.dbms.project.repository.AdminScreenRepository;
import quickshow.dbms.project.repository.AdminTheatreRepository;

import java.util.List;

@Service
public class AdminScreenService {

    private final AdminScreenRepository adminScreenRepository;

    private final AdminTheatreRepository adminTheatreRepository;

    public AdminScreenService(
            AdminScreenRepository adminScreenRepository,
            AdminTheatreRepository adminTheatreRepository
    ) {

        this.adminScreenRepository =
                adminScreenRepository;

        this.adminTheatreRepository =
                adminTheatreRepository;
    }


    // =========================================================
    // GET ALL SCREENS OF THEATRE
    // =========================================================

    public List<AdminScreenDTO> getScreensByTheatre(
            Integer theatreId
    ) {

        return adminScreenRepository.findByTheatreId(
                theatreId
        );
    }


    // =========================================================
    // GET SCREEN BY ID
    // =========================================================

    public AdminScreenDTO getScreenById(
            Integer screenId
    ) {

        return adminScreenRepository.findById(
                screenId
        );
    }


    // =========================================================
    // CREATE SCREEN
    // =========================================================

    public AdminScreenDTO createScreen(
            Integer theatreId,
            AdminScreenDTO screen
    ) {

        /*
         * The theatre must exist before we create
         * a screen belonging to it.
         */

        if (!adminTheatreRepository.existsById(
                theatreId
        )) {
            return null;
        }

        Integer screenId =
                adminScreenRepository.create(
                        theatreId,
                        screen
                );

        return new AdminScreenDTO(
                screenId,
                screen.getName(),
                screen.getScreenType(),
                screen.getCapacity(),
                theatreId
        );
    }


    // =========================================================
    // UPDATE SCREEN
    // =========================================================

    public AdminScreenDTO updateScreen(
            Integer screenId,
            AdminScreenDTO screen
    ) {

        AdminScreenDTO existing =
                adminScreenRepository.findById(
                        screenId
                );

        if (existing == null) {
            return null;
        }

        /*
         * TheatreID is deliberately NOT changed here.
         *
         * A screen belongs to a theatre. Moving a screen
         * between theatres should not happen through a normal
         * update operation.
         */

        adminScreenRepository.update(
                screenId,
                screen
        );

        return new AdminScreenDTO(
                screenId,
                screen.getName(),
                screen.getScreenType(),
                screen.getCapacity(),
                existing.getTheatreId()
        );
    }


    // =========================================================
    // DELETE SCREEN
    // =========================================================

    public String deleteScreen(
            Integer screenId
    ) {

        if (!adminScreenRepository.existsById(
                screenId
        )) {
            return "NOT_FOUND";
        }

        /*
         * Shows must be checked first.
         *
         * A show references this screen and has
         * ShowSeatAllocations associated with it.
         */

        if (adminScreenRepository.hasShows(
                screenId
        )) {
            return "HAS_SHOWS";
        }

        /*
         * Seats reference the screen.
         *
         * Therefore we cannot delete the screen
         * while seats still exist.
         */

        if (adminScreenRepository.hasSeats(
                screenId
        )) {
            return "HAS_SEATS";
        }

        int deleted =
                adminScreenRepository.delete(
                        screenId
                );

        if (deleted == 0) {
            return "NOT_FOUND";
        }

        return "DELETED";
    }
}