package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.BookedSeats;
import quickshow.dbms.project.model.BookedSeatsId;

import java.util.List;

@Repository
public class BookedSeatsRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookedSeatsRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CREATE
    // =========================================================

    public BookedSeats create(
            BookedSeats bookedSeats
    ) {

        String sql = """
                INSERT INTO BookedSeats
                    (
                        BookingID,
                        ShowID,
                        ScreenID,
                        SeatID
                    )
                VALUES
                    (?, ?, ?, ?)
                """;

        BookedSeatsId id =
                bookedSeats.getId();

        jdbcTemplate.update(
                sql,
                id.getBookingId(),
                id.getShowId(),
                id.getScreenId(),
                id.getSeatId()
        );

        return bookedSeats;
    }


    // =========================================================
    // READ BY COMPOSITE ID
    // =========================================================

    public BookedSeats findById(
            BookedSeatsId id
    ) {

        String sql = """
                SELECT
                    BookingID,
                    ShowID,
                    ScreenID,
                    SeatID
                FROM BookedSeats
                WHERE BookingID = ?
                  AND ShowID = ?
                  AND ScreenID = ?
                  AND SeatID = ?
                """;

        List<BookedSeats> bookedSeats =
                jdbcTemplate.query(
                        sql,
                        new BookedSeatsRowMapper(),
                        id.getBookingId(),
                        id.getShowId(),
                        id.getScreenId(),
                        id.getSeatId()
                );

        if (bookedSeats.isEmpty()) {
            return null;
        }

        return bookedSeats.get(0);
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<BookedSeats> findAll() {

        String sql = """
                SELECT
                    BookingID,
                    ShowID,
                    ScreenID,
                    SeatID
                FROM BookedSeats
                ORDER BY
                    BookingID,
                    ShowID,
                    ScreenID,
                    SeatID
                """;

        return jdbcTemplate.query(
                sql,
                new BookedSeatsRowMapper()
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    public int deleteById(
            BookedSeatsId id
    ) {

        String sql = """
                DELETE FROM BookedSeats
                WHERE BookingID = ?
                  AND ShowID = ?
                  AND ScreenID = ?
                  AND SeatID = ?
                """;

        return jdbcTemplate.update(
                sql,
                id.getBookingId(),
                id.getShowId(),
                id.getScreenId(),
                id.getSeatId()
        );
    }


    // =========================================================
    // EXISTS
    // =========================================================

    public boolean existsById(
            BookedSeatsId id
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM BookedSeats
                WHERE BookingID = ?
                  AND ShowID = ?
                  AND ScreenID = ?
                  AND SeatID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        id.getBookingId(),
                        id.getShowId(),
                        id.getScreenId(),
                        id.getSeatId()
                );

        return count != null && count > 0;
    }


    // =========================================================
    // FIND BY BOOKING
    // =========================================================

    public List<BookedSeats> findByBooking(
            Integer bookingId
    ) {

        String sql = """
                SELECT
                    BookingID,
                    ShowID,
                    ScreenID,
                    SeatID
                FROM BookedSeats
                WHERE BookingID = ?
                ORDER BY ShowID, ScreenID, SeatID
                """;

        return jdbcTemplate.query(
                sql,
                new BookedSeatsRowMapper(),
                bookingId
        );
    }


    // =========================================================
    // FIND BY SHOW
    // =========================================================

    public List<BookedSeats> findByShow(
            Integer showId
    ) {

        String sql = """
                SELECT
                    BookingID,
                    ShowID,
                    ScreenID,
                    SeatID
                FROM BookedSeats
                WHERE ShowID = ?
                ORDER BY BookingID, ScreenID, SeatID
                """;

        return jdbcTemplate.query(
                sql,
                new BookedSeatsRowMapper(),
                showId
        );
    }


    // =========================================================
    // FIND BY BOOKING AND SHOW
    // =========================================================

    public List<BookedSeats> findByBookingAndShow(
            Integer bookingId,
            Integer showId
    ) {

        String sql = """
                SELECT
                    BookingID,
                    ShowID,
                    ScreenID,
                    SeatID
                FROM BookedSeats
                WHERE BookingID = ?
                  AND ShowID = ?
                ORDER BY ScreenID, SeatID
                """;

        return jdbcTemplate.query(
                sql,
                new BookedSeatsRowMapper(),
                bookingId,
                showId
        );
    }


    // =========================================================
    // FIND BY SHOW AND SCREEN
    // =========================================================

    public List<BookedSeats> findByShowAndScreen(
            Integer showId,
            Integer screenId
    ) {

        String sql = """
                SELECT
                    BookingID,
                    ShowID,
                    ScreenID,
                    SeatID
                FROM BookedSeats
                WHERE ShowID = ?
                  AND ScreenID = ?
                ORDER BY BookingID, SeatID
                """;

        return jdbcTemplate.query(
                sql,
                new BookedSeatsRowMapper(),
                showId,
                screenId
        );
    }


    // =========================================================
    // DELETE ALL BY BOOKING
    // =========================================================

    public int deleteByBooking(
            Integer bookingId
    ) {

        String sql = """
                DELETE FROM BookedSeats
                WHERE BookingID = ?
                """;

        return jdbcTemplate.update(
                sql,
                bookingId
        );
    }
}