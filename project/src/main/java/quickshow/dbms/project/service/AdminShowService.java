package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import quickshow.dbms.project.dto.AdminShowDTO;
import quickshow.dbms.project.repository.AdminShowRepository;
import quickshow.dbms.project.repository.ShowSeatAllocationRepository;

import java.util.List;

@Service
public class AdminShowService {

    private final AdminShowRepository adminShowRepository;

    private final ShowSeatAllocationRepository
            showSeatAllocationRepository;


    public AdminShowService(
            AdminShowRepository adminShowRepository,
            ShowSeatAllocationRepository showSeatAllocationRepository
    ) {

        this.adminShowRepository =
                adminShowRepository;

        this.showSeatAllocationRepository =
                showSeatAllocationRepository;
    }


    // =========================================================
    // GET ALL
    // =========================================================

    public List<AdminShowDTO> getAllShows() {

        return adminShowRepository.findAll();
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    public AdminShowDTO getShowById(
            Integer showId
    ) {

        return adminShowRepository.findById(
                showId
        );
    }


    // =========================================================
    // CREATE
    // =========================================================

    @Transactional
    public AdminShowDTO createShow(
            AdminShowDTO show
    ) {

        // -----------------------------------------------------
        // Validate required fields
        // -----------------------------------------------------

        if (show.getMovieId() == null) {

            throw new IllegalArgumentException(
                    "Movie ID cannot be null"
            );
        }

        if (show.getScreenId() == null) {

            throw new IllegalArgumentException(
                    "Screen ID cannot be null"
            );
        }

        if (show.getShowDate() == null) {

            throw new IllegalArgumentException(
                    "Show date cannot be null"
            );
        }

        if (show.getShowTime() == null) {

            throw new IllegalArgumentException(
                    "Show time cannot be null"
            );
        }

        if (show.getTicketPrice() == null ||
                show.getTicketPrice() < 0) {

            throw new IllegalArgumentException(
                    "Ticket price must be non-negative"
            );
        }

        if (show.getShowStatus() == null) {

            throw new IllegalArgumentException(
                    "Show status cannot be null"
            );
        }


        // -----------------------------------------------------
        // Movie must exist
        // -----------------------------------------------------

        if (!adminShowRepository.movieExists(
                show.getMovieId()
        )) {

            return null;
        }


        // -----------------------------------------------------
        // Screen must exist
        // -----------------------------------------------------

        if (!adminShowRepository.screenExists(
                show.getScreenId()
        )) {

            return null;
        }


        // -----------------------------------------------------
        // Screen must contain physical seats
        // -----------------------------------------------------

        if (!adminShowRepository.screenHasSeats(
                show.getScreenId()
        )) {

            throw new IllegalStateException(
                    "Selected screen has no seats"
            );
        }


        // -----------------------------------------------------
        // Same screen/date/time conflict
        // -----------------------------------------------------

        if (adminShowRepository.showExistsAtTime(
                show.getScreenId(),
                show.getMovieId(),
                show.getShowDate(),
                show.getShowTime(),
                null
        )) {

            throw new IllegalArgumentException(
                    "A show already exists on this screen at this date and time"
            );
        }


        // -----------------------------------------------------
        // Create Show
        // -----------------------------------------------------

        Integer showId =
                adminShowRepository.create(
                        show
                );


        // -----------------------------------------------------
        // Automatically create allocations
        // -----------------------------------------------------

        int allocations =
                showSeatAllocationRepository
                        .createAllocationsForShow(
                                showId,
                                show.getScreenId()
                        );


        if (allocations == 0) {

            throw new IllegalStateException(
                    "No seats could be allocated to the show"
            );
        }


        // -----------------------------------------------------
        // Calculate available seats
        // -----------------------------------------------------

        int availableSeats =
                showSeatAllocationRepository
                        .countAvailableSeats(
                                showId
                        );


        adminShowRepository.updateAvailableSeats(
                showId,
                availableSeats
        );


        return adminShowRepository.findById(
                showId
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Transactional
    public AdminShowDTO updateShow(
            Integer showId,
            AdminShowDTO show
    ) {

        AdminShowDTO existing =
                adminShowRepository.findById(
                        showId
                );

        if (existing == null) {
            return null;
        }


        // -----------------------------------------------------
        // Validate basic fields
        // -----------------------------------------------------

        if (show.getMovieId() == null ||
                show.getScreenId() == null ||
                show.getShowDate() == null ||
                show.getShowTime() == null ||
                show.getTicketPrice() == null ||
                show.getShowStatus() == null) {

            throw new IllegalArgumentException(
                    "Required show fields cannot be null"
            );
        }

        if (show.getTicketPrice() < 0) {

            throw new IllegalArgumentException(
                    "Ticket price must be non-negative"
            );
        }


        // -----------------------------------------------------
        // Movie must exist
        // -----------------------------------------------------

        if (!adminShowRepository.movieExists(
                show.getMovieId()
        )) {

            return null;
        }


        // -----------------------------------------------------
        // Screen must exist
        // -----------------------------------------------------

        if (!adminShowRepository.screenExists(
                show.getScreenId()
        )) {

            return null;
        }


        // -----------------------------------------------------
        // CHECK BOOKED SEATS
        // -----------------------------------------------------

        boolean hasBookedSeats =
                adminShowRepository.hasBookedSeats(
                        showId
                );


        // =====================================================
        // CASE 1:
        // SHOW HAS BOOKED SEATS
        // =====================================================

        if (hasBookedSeats) {

            /*
             * Once seats are booked, the identity of the
             * show cannot be changed.
             *
             * In particular:
             *
             * ShowDate
             * ShowTime
             * Movie
             * Screen
             *
             * cannot be changed.
             */

            boolean changingDate =
                    !existing.getShowDate().equals(
                            show.getShowDate()
                    );

            boolean changingTime =
                    !existing.getShowTime().equals(
                            show.getShowTime()
                    );

            boolean changingMovie =
                    !existing.getMovieId().equals(
                            show.getMovieId()
                    );

            boolean changingScreen =
                    !existing.getScreenId().equals(
                            show.getScreenId()
                    );


            if (changingDate ||
                    changingTime ||
                    changingMovie ||
                    changingScreen) {

                throw new IllegalStateException(
                        "Show cannot change date, time, movie or screen because seats have already been booked"
                );
            }


            /*
             * Only safe attributes can be changed.
             *
             * TicketPrice
             * ShowStatus
             */

            adminShowRepository.updateWithoutScreen(
                    showId,
                    new AdminShowDTO(
                            showId,
                            existing.getShowDate(),
                            existing.getShowTime(),
                            show.getTicketPrice(),
                            existing.getAvailableSeats(),
                            show.getShowStatus(),
                            existing.getMovieId(),
                            existing.getScreenId()
                    )
            );

            return adminShowRepository.findById(
                    showId
            );
        }


        // =====================================================
        // CASE 2:
        // NO BOOKED SEATS
        // =====================================================

        /*
         * We are allowed to modify the screen because
         * no actual seat has been booked.
         */

        if (adminShowRepository.showExistsAtTime(
                show.getScreenId(),
                show.getMovieId(),
                show.getShowDate(),
                show.getShowTime(),
                showId
        )) {

            throw new IllegalArgumentException(
                    "A show already exists on this screen at this date and time"
            );
        }


        boolean changingScreen =
                !existing.getScreenId().equals(
                        show.getScreenId()
                );


        if (changingScreen) {

            /*
             * Remove allocations belonging to the old screen.
             */

            showSeatAllocationRepository
                    .deleteAllocationsForShow(
                            showId
                    );


            /*
             * Update the Show first.
             */

            adminShowRepository.update(
                    showId,
                    show
            );


            /*
             * Create allocations for the new screen.
             */

            int allocations =
                    showSeatAllocationRepository
                            .createAllocationsForShow(
                                    showId,
                                    show.getScreenId()
                            );


            if (allocations == 0) {

                throw new IllegalStateException(
                        "No seats could be allocated to the new screen"
                );
            }
        }
        else {

            /*
             * Screen hasn't changed.
             * Existing allocations remain valid.
             */

            adminShowRepository.update(
                    showId,
                    show
            );
        }


        // -----------------------------------------------------
        // Recalculate available seats
        // -----------------------------------------------------

        int availableSeats =
                showSeatAllocationRepository
                        .countAvailableSeats(
                                showId
                        );

        adminShowRepository.updateAvailableSeats(
                showId,
                availableSeats
        );


        return adminShowRepository.findById(
                showId
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Transactional
    public String deleteShow(
            Integer showId
    ) {

        if (!adminShowRepository.existsById(
                showId
        )) {

            return "NOT_FOUND";
        }


        // -----------------------------------------------------
        // IMPORTANT: booked seats
        // -----------------------------------------------------

        if (adminShowRepository.hasBookedSeats(
                showId
        )) {

            return "HAS_BOOKED_SEATS";
        }


        /*
         * Also check BookedSeats records.
         *
         * BookedSeats references ShowSeatAllocates directly.
         * Therefore we must not delete allocations that still
         * have booking records referencing them.
         */

        if (adminShowRepository.hasBookedSeatRecords(
                showId
        )) {

            return "HAS_BOOKING_RECORDS";
        }


        // -----------------------------------------------------
        // Delete allocations first
        // -----------------------------------------------------

        showSeatAllocationRepository
                .deleteAllocationsForShow(
                        showId
                );


        // -----------------------------------------------------
        // Delete show
        // -----------------------------------------------------

        int deleted =
                adminShowRepository.delete(
                        showId
                );

        if (deleted == 0) {
            return "NOT_FOUND";
        }

        return "DELETED";
    }
}