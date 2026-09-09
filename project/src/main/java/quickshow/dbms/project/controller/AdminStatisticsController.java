package quickshow.dbms.project.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import quickshow.dbms.project.dto.*;
import quickshow.dbms.project.service.AdminStatisticsService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/statistics")
public class AdminStatisticsController {

    private final AdminStatisticsService
            adminStatisticsService;


    public AdminStatisticsController(
            AdminStatisticsService adminStatisticsService
    ) {

        this.adminStatisticsService =
                adminStatisticsService;
    }


    // =========================================================
    // GET STATISTICS SUMMARY
    //
    // GET /api/admin/statistics/summary
    //
    // Examples:
    //
    // /api/admin/statistics/summary
    //
    // /api/admin/statistics/summary
    //      ?from=2026-08-11
    //      &to=2026-09-09
    //
    // /api/admin/statistics/summary
    //      ?from=2026-08-11
    //      &to=2026-09-09
    //      &theatreId=3
    //
    // /api/admin/statistics/summary
    //      ?from=2026-08-11
    //      &to=2026-09-09
    //      &theatreId=3
    //      &movieId=7
    // =========================================================

    @GetMapping("/summary")
    public ResponseEntity<AdminStatisticsSummaryDTO>
    getSummary(

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate to,

            @RequestParam(required = false)
            Integer theatreId,

            @RequestParam(required = false)
            Integer movieId
    ) {

        /*
         * Dashboard default:
         *
         * Last 30 days.
         *
         * Example:
         *
         * 9 September
         * ↓
         * 12 August - 9 September
         *
         * 30 calendar days inclusive.
         */

        if (to == null) {
            to = LocalDate.now();
        }

        if (from == null) {
            from = to.minusDays(29);
        }


        AdminStatisticsSummaryDTO summary =
                adminStatisticsService.getSummary(
                        from,
                        to,
                        theatreId,
                        movieId
                );


        return ResponseEntity.ok(summary);
    }

    @GetMapping("/revenue")
    public ResponseEntity<RevenueStatisticsDTO> getRevenue(

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate to,

            @RequestParam(required = false)
            Integer theatreId,

            @RequestParam(required = false)
            Integer movieId
    ) {

        if (to == null) {
            to = LocalDate.now();
        }

        if (from == null) {
            from = to.minusDays(29);
        }

        return ResponseEntity.ok(
                adminStatisticsService.getRevenueStatistics(
                        from,
                        to,
                        theatreId,
                        movieId
                )
        );
    }

    @GetMapping("/bookings")
    public ResponseEntity<BookingStatisticsDTO> getBookings(

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate to,

            @RequestParam(required = false)
            Integer theatreId,

            @RequestParam(required = false)
            Integer movieId
    ) {

        if (to == null) {
            to = LocalDate.now();
        }

        if (from == null) {
            from = to.minusDays(29);
        }

        return ResponseEntity.ok(
                adminStatisticsService.getBookingStatistics(
                        from,
                        to,
                        theatreId,
                        movieId
                )
        );
    }

    @GetMapping("/movies/top")
    public ResponseEntity<List<MoviePerformanceDTO>>
    getTopMovies(

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate to,

            @RequestParam(required = false)
            Integer theatreId,

            @RequestParam(required = false)
            Integer limit
    ) {

        if (to == null) {
            to = LocalDate.now();
        }

        if (from == null) {
            from = to.minusDays(29);
        }

        return ResponseEntity.ok(
                adminStatisticsService.getTopMovies(
                        from,
                        to,
                        theatreId,
                        limit
                )
        );
    }

    @GetMapping("/theatres")
    public ResponseEntity<List<TheatrePerformanceDTO>>
    getTheatrePerformance(

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate to,

            @RequestParam(required = false)
            Integer movieId
    ) {

        if (to == null) {
            to = LocalDate.now();
        }

        if (from == null) {
            from = to.minusDays(29);
        }

        return ResponseEntity.ok(
                adminStatisticsService
                        .getTheatrePerformance(
                                from,
                                to,
                                movieId
                        )
        );
    }
}