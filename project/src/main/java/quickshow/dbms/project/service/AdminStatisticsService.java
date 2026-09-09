package quickshow.dbms.project.service;

import org.springframework.stereotype.Service;

import quickshow.dbms.project.dto.*;
import quickshow.dbms.project.repository.AdminStatisticsRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdminStatisticsService {

    private final AdminStatisticsRepository
            adminStatisticsRepository;


    public AdminStatisticsService(
            AdminStatisticsRepository adminStatisticsRepository
    ) {

        this.adminStatisticsRepository =
                adminStatisticsRepository;
    }


    // =========================================================
    // GET STATISTICS SUMMARY
    // =========================================================

    public AdminStatisticsSummaryDTO getSummary(
            LocalDate from,
            LocalDate to,
            Integer theatreId,
            Integer movieId
    ) {

        // -----------------------------------------------------
        // DATE VALIDATION
        // -----------------------------------------------------

        if (from == null) {
            throw new IllegalArgumentException(
                    "'from' date cannot be null"
            );
        }

        if (to == null) {
            throw new IllegalArgumentException(
                    "'to' date cannot be null"
            );
        }

        if (from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "'from' date cannot be after 'to' date"
            );
        }


        // -----------------------------------------------------
        // THEATRE ID VALIDATION
        // -----------------------------------------------------

        if (theatreId != null && theatreId <= 0) {
            throw new IllegalArgumentException(
                    "Theatre ID must be greater than zero"
            );
        }


        // -----------------------------------------------------
        // MOVIE ID VALIDATION
        // -----------------------------------------------------

        if (movieId != null && movieId <= 0) {
            throw new IllegalArgumentException(
                    "Movie ID must be greater than zero"
            );
        }


        return adminStatisticsRepository.getSummary(
                from,
                to,
                theatreId,
                movieId
        );
    }

    // =========================================================
// REVENUE
// =========================================================

    public RevenueStatisticsDTO getRevenueStatistics(
            LocalDate from,
            LocalDate to,
            Integer theatreId,
            Integer movieId
    ) {

        validateFilters(
                from,
                to,
                theatreId,
                movieId
        );

        return adminStatisticsRepository
                .getRevenueStatistics(
                        from,
                        to,
                        theatreId,
                        movieId
                );
    }


// =========================================================
// BOOKINGS
// =========================================================

    public BookingStatisticsDTO getBookingStatistics(
            LocalDate from,
            LocalDate to,
            Integer theatreId,
            Integer movieId
    ) {

        validateFilters(
                from,
                to,
                theatreId,
                movieId
        );

        return adminStatisticsRepository
                .getBookingStatistics(
                        from,
                        to,
                        theatreId,
                        movieId
                );
    }


// =========================================================
// TOP MOVIES
// =========================================================

    public List<MoviePerformanceDTO> getTopMovies(
            LocalDate from,
            LocalDate to,
            Integer theatreId,
            Integer limit
    ) {

        validateFilters(
                from,
                to,
                theatreId,
                null
        );


        if (limit == null) {
            limit = 5;
        }

        if (limit <= 0) {
            throw new IllegalArgumentException(
                    "Limit must be greater than zero"
            );
        }

        if (limit > 50) {
            throw new IllegalArgumentException(
                    "Limit cannot be greater than 50"
            );
        }


        return adminStatisticsRepository.getTopMovies(
                from,
                to,
                theatreId,
                limit
        );
    }


// =========================================================
// THEATRE PERFORMANCE
// =========================================================

    public List<TheatrePerformanceDTO> getTheatrePerformance(
            LocalDate from,
            LocalDate to,
            Integer movieId
    ) {

        validateFilters(
                from,
                to,
                null,
                movieId
        );

        return adminStatisticsRepository
                .getTheatrePerformance(
                        from,
                        to,
                        movieId
                );
    }


// =========================================================
// VALIDATION
// =========================================================

    private void validateFilters(
            LocalDate from,
            LocalDate to,
            Integer theatreId,
            Integer movieId
    ) {

        if (from == null) {
            throw new IllegalArgumentException(
                    "'from' date cannot be null"
            );
        }

        if (to == null) {
            throw new IllegalArgumentException(
                    "'to' date cannot be null"
            );
        }

        if (from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "'from' date cannot be after 'to' date"
            );
        }

        if (theatreId != null && theatreId <= 0) {
            throw new IllegalArgumentException(
                    "Theatre ID must be greater than zero"
            );
        }

        if (movieId != null && movieId <= 0) {
            throw new IllegalArgumentException(
                    "Movie ID must be greater than zero"
            );
        }
    }
}