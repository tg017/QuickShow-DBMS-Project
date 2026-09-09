package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import quickshow.dbms.project.dto.*;

import java.time.LocalDate;
import java.util.List;

@Repository
public class AdminStatisticsRepository {

    private final JdbcTemplate jdbcTemplate;


    public AdminStatisticsRepository(
            JdbcTemplate jdbcTemplate
    ) {

        this.jdbcTemplate =
                jdbcTemplate;
    }


    // =========================================================
    // ADMIN STATISTICS SUMMARY
    // =========================================================

    public AdminStatisticsSummaryDTO getSummary(
            LocalDate from,
            LocalDate to,
            Integer theatreId,
            Integer movieId
    ) {

        String sql = """
                WITH FilteredShows AS (

                    SELECT
                        sh.ShowID,
                        sh.MovieID,
                        sh.ScreenID,
                        sh.ShowStatus,
                        sc.TheatreID

                    FROM `Show` sh

                    JOIN Screen sc
                        ON sh.ScreenID = sc.ScreenID

                    WHERE sh.ShowDate BETWEEN ? AND ?

                      AND (
                          ? IS NULL
                          OR sc.TheatreID = ?
                      )

                      AND (
                          ? IS NULL
                          OR sh.MovieID = ?
                      )
                ),


                FilteredBookings AS (

                    SELECT DISTINCT

                        b.BookingID,
                        b.UserID,
                        b.BookingStatus,
                        b.TotalSeatsCount

                    FROM Booking b

                    JOIN BookedSeats bs
                        ON b.BookingID = bs.BookingID

                    JOIN FilteredShows fs
                        ON bs.ShowID = fs.ShowID
                ),


                Revenue AS (

                    SELECT
                        COALESCE(
                            SUM(p.PaymentAmount),
                            0
                        ) AS TotalRevenue

                    FROM Payment p

                    JOIN FilteredBookings fb
                        ON p.BookingID = fb.BookingID

                    WHERE p.PaymentStatus = 'SUCCESS'
                )


                SELECT

                    /* =========================================
                       TOTAL REVENUE
                       ========================================= */

                    r.TotalRevenue,


                    /* =========================================
                       TOTAL BOOKINGS
                       ========================================= */

                    (
                        SELECT COUNT(*)
                        FROM FilteredBookings
                    ) AS TotalBookings,


                    /* =========================================
                       TICKETS SOLD
                       ========================================= */

                    (
                        SELECT COALESCE(
                            SUM(
                                CASE
                                    WHEN BookingStatus = 'CONFIRMED'
                                    THEN TotalSeatsCount
                                    ELSE 0
                                END
                            ),
                            0
                        )

                        FROM FilteredBookings
                    ) AS TicketsSold,


                    /* =========================================
                       ACTIVE SHOWS
                       ========================================= */

                    (
                        SELECT COUNT(*)

                        FROM FilteredShows

                        WHERE ShowStatus IN (
                            'SCHEDULED',
                            'ONGOING'
                        )
                    ) AS ActiveShows,


                    /* =========================================
                       MOVIES
                       ========================================= */

                    (
                        SELECT COUNT(
                            DISTINCT MovieID
                        )

                        FROM FilteredShows
                    ) AS TotalMovies,


                    /* =========================================
                       THEATRES
                       ========================================= */

                    (
                        SELECT COUNT(
                            DISTINCT TheatreID
                        )

                        FROM FilteredShows
                    ) AS TotalTheatres,


                    /* =========================================
                       CUSTOMERS
                       ========================================= */

                    (
                        SELECT COUNT(
                            DISTINCT UserID
                        )

                        FROM FilteredBookings
                    ) AS TotalCustomers


                FROM Revenue r
                """;


        List<AdminStatisticsSummaryDTO> result =
                jdbcTemplate.query(
                        sql,

                        (rs, rowNum) ->
                                new AdminStatisticsSummaryDTO(

                                        rs.getBigDecimal(
                                                "TotalRevenue"
                                        ),

                                        rs.getLong(
                                                "TotalBookings"
                                        ),

                                        rs.getLong(
                                                "TicketsSold"
                                        ),

                                        rs.getLong(
                                                "ActiveShows"
                                        ),

                                        rs.getLong(
                                                "TotalMovies"
                                        ),

                                        rs.getLong(
                                                "TotalTheatres"
                                        ),

                                        rs.getLong(
                                                "TotalCustomers"
                                        )
                                ),

                        from,
                        to,

                        theatreId,
                        theatreId,

                        movieId,
                        movieId
                );


        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    // =========================================================
// REVENUE STATISTICS
// =========================================================

    public RevenueStatisticsDTO getRevenueStatistics(
            LocalDate from,
            LocalDate to,
            Integer theatreId,
            Integer movieId
    ) {

        String sql = """
            WITH FilteredBookings AS (

                SELECT DISTINCT

                    b.BookingID,
                    b.UserID,
                    b.BookingStatus,
                    b.TotalSeatsCount

                FROM Booking b

                JOIN BookedSeats bs
                    ON b.BookingID = bs.BookingID

                JOIN `Show` sh
                    ON bs.ShowID = sh.ShowID

                JOIN Screen sc
                    ON sh.ScreenID = sc.ScreenID

                WHERE sh.ShowDate BETWEEN ? AND ?

                  AND (
                      ? IS NULL
                      OR sc.TheatreID = ?
                  )

                  AND (
                      ? IS NULL
                      OR sh.MovieID = ?
                  )
            ),


            RevenueData AS (

                SELECT

                    COALESCE(
                        SUM(
                            CASE
                                WHEN p.PaymentStatus = 'SUCCESS'
                                THEN p.PaymentAmount
                                ELSE 0
                            END
                        ),
                        0
                    ) AS TotalRevenue,

                    COALESCE(
                        SUM(
                            CASE
                                WHEN p.PaymentStatus = 'SUCCESS'
                                THEN 1
                                ELSE 0
                            END
                        ),
                        0
                    ) AS SuccessfulPayments,

                    COALESCE(
                        SUM(
                            CASE
                                WHEN p.PaymentStatus = 'FAILED'
                                THEN 1
                                ELSE 0
                            END
                        ),
                        0
                    ) AS FailedPayments

                FROM Payment p

                JOIN FilteredBookings fb
                    ON p.BookingID = fb.BookingID
            ),


            BookingData AS (

                SELECT

                    COUNT(
                        CASE
                            WHEN BookingStatus = 'CONFIRMED'
                            THEN 1
                        END
                    ) AS ConfirmedBookings,

                    COUNT(
                        CASE
                            WHEN BookingStatus = 'CANCELLED'
                            THEN 1
                        END
                    ) AS CancelledBookings,

                    COALESCE(
                        SUM(
                            CASE
                                WHEN BookingStatus = 'CONFIRMED'
                                THEN TotalSeatsCount
                                ELSE 0
                            END
                        ),
                        0
                    ) AS ConfirmedTickets

                FROM FilteredBookings
            )


            SELECT

                r.TotalRevenue,

                COALESCE(
                    r.TotalRevenue
                    /
                    NULLIF(
                        b.ConfirmedBookings,
                        0
                    ),
                    0
                ) AS AverageBookingValue,


                COALESCE(
                    r.TotalRevenue
                    /
                    NULLIF(
                        b.ConfirmedTickets,
                        0
                    ),
                    0
                ) AS AverageTicketPrice,


                r.SuccessfulPayments,

                r.FailedPayments,

                b.CancelledBookings

            FROM RevenueData r

            CROSS JOIN BookingData b
            """;


        return jdbcTemplate.queryForObject(
                sql,

                (rs, rowNum) ->
                        new RevenueStatisticsDTO(

                                rs.getBigDecimal(
                                        "TotalRevenue"
                                ),

                                rs.getBigDecimal(
                                        "AverageBookingValue"
                                ),

                                rs.getBigDecimal(
                                        "AverageTicketPrice"
                                ),

                                rs.getLong(
                                        "SuccessfulPayments"
                                ),

                                rs.getLong(
                                        "FailedPayments"
                                ),

                                rs.getLong(
                                        "CancelledBookings"
                                )
                        ),

                from,
                to,

                theatreId,
                theatreId,

                movieId,
                movieId
        );
    }

    // =========================================================
// BOOKING STATISTICS
// =========================================================

    public BookingStatisticsDTO getBookingStatistics(
            LocalDate from,
            LocalDate to,
            Integer theatreId,
            Integer movieId
    ) {

        String sql = """
            WITH FilteredBookings AS (

                SELECT DISTINCT

                    b.BookingID,
                    b.BookingStatus,
                    b.TotalSeatsCount

                FROM Booking b

                JOIN BookedSeats bs
                    ON b.BookingID = bs.BookingID

                JOIN `Show` sh
                    ON bs.ShowID = sh.ShowID

                JOIN Screen sc
                    ON sh.ScreenID = sc.ScreenID

                WHERE sh.ShowDate BETWEEN ? AND ?

                  AND (
                      ? IS NULL
                      OR sc.TheatreID = ?
                  )

                  AND (
                      ? IS NULL
                      OR sh.MovieID = ?
                  )
            )


            SELECT

                COUNT(*) AS TotalBookings,


                COALESCE(
                    SUM(
                        CASE
                            WHEN BookingStatus = 'CONFIRMED'
                            THEN 1
                            ELSE 0
                        END
                    ),
                    0
                ) AS ConfirmedBookings,


                COALESCE(
                    SUM(
                        CASE
                            WHEN BookingStatus = 'CANCELLED'
                            THEN 1
                            ELSE 0
                        END
                    ),
                    0
                ) AS CancelledBookings,


                COALESCE(
                    SUM(
                        CASE
                            WHEN BookingStatus = 'PENDING'
                            THEN 1
                            ELSE 0
                        END
                    ),
                    0
                ) AS PendingBookings,


                COALESCE(
                    SUM(
                        CASE
                            WHEN BookingStatus = 'CONFIRMED'
                            THEN TotalSeatsCount
                            ELSE 0
                        END
                    ),
                    0
                ) AS TicketsSold,


                COALESCE(
                    SUM(
                        CASE
                            WHEN BookingStatus = 'CANCELLED'
                            THEN TotalSeatsCount
                            ELSE 0
                        END
                    ),
                    0
                ) AS CancelledTickets,


                COALESCE(
                    AVG(
                        CASE
                            WHEN BookingStatus = 'CONFIRMED'
                            THEN TotalSeatsCount
                        END
                    ),
                    0
                ) AS AverageSeatsPerBooking


            FROM FilteredBookings
            """;


        return jdbcTemplate.queryForObject(
                sql,

                (rs, rowNum) ->
                        new BookingStatisticsDTO(

                                rs.getLong(
                                        "TotalBookings"
                                ),

                                rs.getLong(
                                        "ConfirmedBookings"
                                ),

                                rs.getLong(
                                        "CancelledBookings"
                                ),

                                rs.getLong(
                                        "PendingBookings"
                                ),

                                rs.getLong(
                                        "TicketsSold"
                                ),

                                rs.getLong(
                                        "CancelledTickets"
                                ),

                                rs.getDouble(
                                        "AverageSeatsPerBooking"
                                )
                        ),

                from,
                to,

                theatreId,
                theatreId,

                movieId,
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

        String sql = """
            WITH MovieSales AS (

                SELECT

                    sh.MovieID,

                    SUM(
                        CASE
                            WHEN b.BookingStatus = 'CONFIRMED'
                            THEN b.TotalSeatsCount
                            ELSE 0
                        END
                    ) AS TicketsSold,


                    COUNT(
                        DISTINCT CASE
                            WHEN b.BookingStatus = 'CONFIRMED'
                            THEN b.BookingID
                        END
                    ) AS Bookings

                FROM `Show` sh

                JOIN Screen sc
                    ON sh.ScreenID = sc.ScreenID

                JOIN BookedSeats bs
                    ON bs.ShowID = sh.ShowID

                JOIN Booking b
                    ON b.BookingID = bs.BookingID

                WHERE sh.ShowDate BETWEEN ? AND ?

                  AND (
                      ? IS NULL
                      OR sc.TheatreID = ?
                  )

                GROUP BY sh.MovieID
            ),


            MovieRevenue AS (

                SELECT

                    sh.MovieID,

                    SUM(
                        p.PaymentAmount
                    ) AS Revenue

                FROM `Show` sh

                JOIN Screen sc
                    ON sh.ScreenID = sc.ScreenID

                JOIN (
                    SELECT DISTINCT

                        bs.BookingID,
                        bs.ShowID

                    FROM BookedSeats bs
                ) bs

                    ON bs.ShowID = sh.ShowID

                JOIN Payment p
                    ON p.BookingID = bs.BookingID

                WHERE sh.ShowDate BETWEEN ? AND ?

                  AND p.PaymentStatus = 'SUCCESS'

                  AND (
                      ? IS NULL
                      OR sc.TheatreID = ?
                  )

                GROUP BY sh.MovieID
            )


            SELECT

                m.MovieID,

                m.Title,

                COALESCE(
                    ms.TicketsSold,
                    0
                ) AS TicketsSold,

                COALESCE(
                    ms.Bookings,
                    0
                ) AS Bookings,

                COALESCE(
                    mr.Revenue,
                    0
                ) AS Revenue


            FROM Movie m

            JOIN MovieSales ms
                ON ms.MovieID = m.MovieID

            LEFT JOIN MovieRevenue mr
                ON mr.MovieID = m.MovieID


            ORDER BY
                TicketsSold DESC,
                Revenue DESC


            LIMIT ?
            """;


        return jdbcTemplate.query(
                sql,

                (rs, rowNum) ->
                        new MoviePerformanceDTO(

                                rs.getInt(
                                        "MovieID"
                                ),

                                rs.getString(
                                        "Title"
                                ),

                                rs.getLong(
                                        "TicketsSold"
                                ),

                                rs.getLong(
                                        "Bookings"
                                ),

                                rs.getBigDecimal(
                                        "Revenue"
                                )
                        ),

                from,
                to,

                theatreId,
                theatreId,

                from,
                to,

                theatreId,
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

        String sql = """
            WITH TheatreShows AS (

                SELECT

                    sc.TheatreID,

                    COUNT(
                        DISTINCT sh.ShowID
                    ) AS TotalShows

                FROM Screen sc

                JOIN `Show` sh
                    ON sh.ScreenID = sc.ScreenID

                WHERE sh.ShowDate BETWEEN ? AND ?

                  AND (
                      ? IS NULL
                      OR sh.MovieID = ?
                  )

                GROUP BY sc.TheatreID
            ),


            TheatreSales AS (

                SELECT

                    sc.TheatreID,

                    SUM(
                        CASE
                            WHEN b.BookingStatus = 'CONFIRMED'
                            THEN b.TotalSeatsCount
                            ELSE 0
                        END
                    ) AS TicketsSold

                FROM Screen sc

                JOIN `Show` sh
                    ON sh.ScreenID = sc.ScreenID

                JOIN BookedSeats bs
                    ON bs.ShowID = sh.ShowID

                JOIN Booking b
                    ON b.BookingID = bs.BookingID

                WHERE sh.ShowDate BETWEEN ? AND ?

                  AND (
                      ? IS NULL
                      OR sh.MovieID = ?
                  )

                GROUP BY sc.TheatreID
            ),


            TheatreRevenue AS (

                SELECT

                    sc.TheatreID,

                    SUM(
                        p.PaymentAmount
                    ) AS Revenue

                FROM Screen sc

                JOIN `Show` sh
                    ON sh.ScreenID = sc.ScreenID

                JOIN (
                    SELECT DISTINCT

                        bs.BookingID,
                        bs.ShowID

                    FROM BookedSeats bs
                ) bs

                    ON bs.ShowID = sh.ShowID

                JOIN Payment p
                    ON p.BookingID = bs.BookingID

                WHERE sh.ShowDate BETWEEN ? AND ?

                  AND p.PaymentStatus = 'SUCCESS'

                  AND (
                      ? IS NULL
                      OR sh.MovieID = ?
                  )

                GROUP BY sc.TheatreID
            )


            SELECT

                t.TheatreID,

                t.Name AS TheatreName,

                COALESCE(
                    ts.TotalShows,
                    0
                ) AS TotalShows,

                COALESCE(
                    sales.TicketsSold,
                    0
                ) AS TicketsSold,

                COALESCE(
                    revenue.Revenue,
                    0
                ) AS Revenue


            FROM Theatre t

            JOIN TheatreShows ts
                ON ts.TheatreID = t.TheatreID

            LEFT JOIN TheatreSales sales
                ON sales.TheatreID = t.TheatreID

            LEFT JOIN TheatreRevenue revenue
                ON revenue.TheatreID = t.TheatreID


            ORDER BY
                Revenue DESC
            """;


        return jdbcTemplate.query(
                sql,

                (rs, rowNum) ->
                        new TheatrePerformanceDTO(

                                rs.getInt(
                                        "TheatreID"
                                ),

                                rs.getString(
                                        "TheatreName"
                                ),

                                rs.getLong(
                                        "TotalShows"
                                ),

                                rs.getLong(
                                        "TicketsSold"
                                ),

                                rs.getBigDecimal(
                                        "Revenue"
                                )
                        ),

                from,
                to,

                movieId,
                movieId,

                from,
                to,

                movieId,
                movieId,

                from,
                to,

                movieId,
                movieId
        );
    }
}