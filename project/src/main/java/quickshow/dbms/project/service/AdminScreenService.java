package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import quickshow.dbms.project.dto.AdminScreenDTO;
import quickshow.dbms.project.dto.SeatRowConfigDTO;
import quickshow.dbms.project.repository.AdminScreenRepository;
import quickshow.dbms.project.repository.AdminTheatreRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    // CREATE SCREEN + SEATS
    // =========================================================

    @Transactional
    public AdminScreenDTO createScreen(
            Integer theatreId,
            AdminScreenDTO screen
    ) {

        /*
         * Theatre must exist.
         */

        if (!adminTheatreRepository.existsById(
                theatreId
        )) {

            return null;
        }


        /*
         * Validate row configuration.
         */

        if (!isValidRows(
                screen.getRows()
        )) {

            throw new IllegalArgumentException(
                    "Invalid seat row configuration"
            );
        }


        /*
         * The total number of generated seats must
         * equal the screen capacity.
         */

        int totalSeats = 0;

        for (SeatRowConfigDTO row : screen.getRows()) {

            totalSeats += row.getSeatCount();
        }

        if (totalSeats != screen.getCapacity()) {

            throw new IllegalArgumentException(
                    "Total seats in rows must equal screen capacity"
            );
        }


        /*
         * Create the screen first.
         */

        Integer screenId =
                adminScreenRepository.createScreen(
                        theatreId,
                        screen
                );


        /*
         * Now generate all physical seats.
         */

        adminScreenRepository.createSeats(
                screenId,
                screen.getRows()
        );


        /*
         * Return created screen.
         */

        return new AdminScreenDTO(
                screenId,
                screen.getName(),
                screen.getScreenType(),
                screen.getCapacity(),
                theatreId,
                screen.getRows()
        );
    }


    // =========================================================
    // VALIDATE ROW CONFIGURATION
    // =========================================================

    private boolean isValidRows(
            List<SeatRowConfigDTO> rows
    ) {

        if (rows == null || rows.isEmpty()) {
            return false;
        }

        Set<String> rowNames =
                new HashSet<>();

        for (SeatRowConfigDTO row : rows) {

            if (row == null) {
                return false;
            }

            if (row.getRowNo() == null ||
                    row.getRowNo().trim().isEmpty()) {

                return false;
            }

            if (row.getSeatCount() == null ||
                    row.getSeatCount() <= 0) {

                return false;
            }

            String rowName =
                    row.getRowNo().trim();

            if (!rowNames.add(rowName)) {

                return false;
            }
        }

        return true;
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
         * TheatreID is deliberately not changed.
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
                existing.getTheatreId(),
                null
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


        if (adminScreenRepository.hasShows(
                screenId
        )) {

            return "HAS_SHOWS";
        }


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