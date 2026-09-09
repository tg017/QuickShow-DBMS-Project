package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.dto.AdminBookingListDTO;

import java.util.List;

@Repository
public class AdminBookingRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminBookingRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // GET ALL BOOKINGS
    // =========================================================

    public List<AdminBookingListDTO> findAll() {

        String sql = """
                SELECT
                    b.BookingID,
                    m.Title AS MovieName,
                    t.Name AS TheatreName,
                    b.BookingStatus,
                    b.TotalAmount

                FROM Booking b

                JOIN BookedSeats bs
                    ON b.BookingID = bs.BookingID

                JOIN `Show` sh
                    ON bs.ShowID = sh.ShowID

                JOIN Movie m
                    ON sh.MovieID = m.MovieID

                JOIN Screen sc
                    ON sh.ScreenID = sc.ScreenID

                JOIN Theatre t
                    ON sc.TheatreID = t.TheatreID

                GROUP BY
                    b.BookingID,
                    m.Title,
                    t.Name,
                    b.BookingStatus,
                    b.TotalAmount

                ORDER BY
                    b.BookingID DESC
                """;

        return jdbcTemplate.query(
                sql,
                new AdminBookingRowMapper()
        );
    }


    // =========================================================
    // GET BOOKINGS BY MOVIE
    // =========================================================

    public List<AdminBookingListDTO> findByMovie(
            Integer movieId
    ) {

        String sql = """
                SELECT
                    b.BookingID,
                    m.Title AS MovieName,
                    t.Name AS TheatreName,
                    b.BookingStatus,
                    b.TotalAmount

                FROM Booking b

                JOIN BookedSeats bs
                    ON b.BookingID = bs.BookingID

                JOIN `Show` sh
                    ON bs.ShowID = sh.ShowID

                JOIN Movie m
                    ON sh.MovieID = m.MovieID

                JOIN Screen sc
                    ON sh.ScreenID = sc.ScreenID

                JOIN Theatre t
                    ON sc.TheatreID = t.TheatreID

                WHERE m.MovieID = ?

                GROUP BY
                    b.BookingID,
                    m.Title,
                    t.Name,
                    b.BookingStatus,
                    b.TotalAmount

                ORDER BY
                    b.BookingID DESC
                """;

        return jdbcTemplate.query(
                sql,
                new AdminBookingRowMapper(),
                movieId
        );
    }


    // =========================================================
    // GET BOOKINGS BY THEATRE
    // =========================================================

    public List<AdminBookingListDTO> findByTheatre(
            Integer theatreId
    ) {

        String sql = """
                SELECT
                    b.BookingID,
                    m.Title AS MovieName,
                    t.Name AS TheatreName,
                    b.BookingStatus,
                    b.TotalAmount

                FROM Booking b

                JOIN BookedSeats bs
                    ON b.BookingID = bs.BookingID

                JOIN `Show` sh
                    ON bs.ShowID = sh.ShowID

                JOIN Movie m
                    ON sh.MovieID = m.MovieID

                JOIN Screen sc
                    ON sh.ScreenID = sc.ScreenID

                JOIN Theatre t
                    ON sc.TheatreID = t.TheatreID

                WHERE t.TheatreID = ?

                GROUP BY
                    b.BookingID,
                    m.Title,
                    t.Name,
                    b.BookingStatus,
                    b.TotalAmount

                ORDER BY
                    b.BookingID DESC
                """;

        return jdbcTemplate.query(
                sql,
                new AdminBookingRowMapper(),
                theatreId
        );
    }


    // =========================================================
    // GET BOOKINGS BY MOVIE + THEATRE
    // =========================================================

    public List<AdminBookingListDTO> findByMovieAndTheatre(
            Integer movieId,
            Integer theatreId
    ) {

        String sql = """
                SELECT
                    b.BookingID,
                    m.Title AS MovieName,
                    t.Name AS TheatreName,
                    b.BookingStatus,
                    b.TotalAmount

                FROM Booking b

                JOIN BookedSeats bs
                    ON b.BookingID = bs.BookingID

                JOIN `Show` sh
                    ON bs.ShowID = sh.ShowID

                JOIN Movie m
                    ON sh.MovieID = m.MovieID

                JOIN Screen sc
                    ON sh.ScreenID = sc.ScreenID

                JOIN Theatre t
                    ON sc.TheatreID = t.TheatreID

                WHERE m.MovieID = ?
                  AND t.TheatreID = ?

                GROUP BY
                    b.BookingID,
                    m.Title,
                    t.Name,
                    b.BookingStatus,
                    b.TotalAmount

                ORDER BY
                    b.BookingID DESC
                """;

        return jdbcTemplate.query(
                sql,
                new AdminBookingRowMapper(),
                movieId,
                theatreId
        );
    }
}