package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;
import quickshow.dbms.project.dto.AdminBookingListDTO;
import quickshow.dbms.project.repository.AdminBookingRepository;

import java.util.List;

@Service
public class AdminBookingService {

    private final AdminBookingRepository adminBookingRepository;


    public AdminBookingService(
            AdminBookingRepository adminBookingRepository
    ) {

        this.adminBookingRepository =
                adminBookingRepository;
    }


    // =========================================================
    // GET ALL BOOKINGS
    // =========================================================

    public List<AdminBookingListDTO> getAllBookings() {

        return adminBookingRepository.findAll();
    }


    // =========================================================
    // FILTER BY MOVIE
    // =========================================================

    public List<AdminBookingListDTO> getBookingsByMovie(
            Integer movieId
    ) {

        return adminBookingRepository.findByMovie(
                movieId
        );
    }


    // =========================================================
    // FILTER BY THEATRE
    // =========================================================

    public List<AdminBookingListDTO> getBookingsByTheatre(
            Integer theatreId
    ) {

        return adminBookingRepository.findByTheatre(
                theatreId
        );
    }


    // =========================================================
    // FILTER BY MOVIE + THEATRE
    // =========================================================

    public List<AdminBookingListDTO> getBookingsByMovieAndTheatre(
            Integer movieId,
            Integer theatreId
    ) {

        return adminBookingRepository.findByMovieAndTheatre(
                movieId,
                theatreId
        );
    }
}