package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.repository.data.BookingDetailsData;
import quickshow.dbms.project.repository.data.CheckoutShowData;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class BookingRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookingRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CHECK CUSTOMER
    // =========================================================

    public boolean customerExists(
            Integer customerId
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM Customer
                WHERE UserId = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        customerId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // GET SHOW INFORMATION REQUIRED FOR CHECKOUT
    // =========================================================

    public CheckoutShowData findShowForCheckout(
            Integer showId
    ) {

        String sql = """
                SELECT
                    sh.ShowID,
                    sh.ScreenID,
                    sh.MovieID,
                    sh.TicketPrice,
                    sh.AvailableSeats,
                    sh.ShowStatus

                FROM `Show` sh

                WHERE sh.ShowID = ?
                """;

        List<CheckoutShowData> results =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) ->
                                new CheckoutShowData(
                                        rs.getInt("ShowID"),
                                        rs.getInt("ScreenID"),
                                        rs.getInt("MovieID"),
                                        rs.getLong("TicketPrice"),
                                        rs.getInt("AvailableSeats"),
                                        rs.getString("ShowStatus")
                                ),
                        showId
                );

        if (results.isEmpty()) {
            return null;
        }

        return results.get(0);
    }


    // =========================================================
    // CREATE BOOKING
    // =========================================================

    public Integer createBooking(
            Integer customerId,
            Long totalAmount,
            Integer totalSeatCount
    ) {

        String sql = """
                INSERT INTO Booking
                (
                    UserID,
                    BookingDateTime,
                    BookingStatus,
                    TotalAmount,
                    TotalSeatsCount
                )
                VALUES
                (?, NOW(), 'PENDING', ?, ?)
                """;

        KeyHolder keyHolder =
                new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            statement.setInt(1, customerId);
            statement.setLong(2, totalAmount);
            statement.setInt(3, totalSeatCount);

            return statement;

        }, keyHolder);

        Number generatedId =
                keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException(
                    "BookingID was not generated"
            );
        }

        return generatedId.intValue();
    }


    // =========================================================
    // CONFIRM BOOKING
    // =========================================================

    public int confirmBooking(
            Integer bookingId
    ) {

        String sql = """
                UPDATE Booking
                SET BookingStatus = 'CONFIRMED'
                WHERE BookingID = ?
                """;

        return jdbcTemplate.update(
                sql,
                bookingId
        );
    }


    // =========================================================
    // MARK BOOKING FAILED
    // =========================================================

    public int failBooking(
            Integer bookingId
    ) {

        String sql = """
                UPDATE Booking
                SET BookingStatus = 'FAILED'
                WHERE BookingID = ?
                """;

        return jdbcTemplate.update(
                sql,
                bookingId
        );
    }


    // =========================================================
    // UPDATE SHOW AVAILABLE SEATS
    // =========================================================

    public int decreaseAvailableSeats(
            Integer showId,
            Integer seatCount
    ) {

        String sql = """
                UPDATE `Show`
                SET AvailableSeats =
                    AvailableSeats - ?
                WHERE ShowID = ?
                  AND AvailableSeats >= ?
                """;

        return jdbcTemplate.update(
                sql,
                seatCount,
                showId,
                seatCount
        );
    }


    // =========================================================
    // GET BOOKING DETAILS
    // =========================================================

    public BookingDetailsData findBookingDetails(
            Integer bookingId
    ) {

        String sql = """
                SELECT
                    b.BookingID,
                    b.TotalAmount,
                    b.TotalSeatsCount,
                    b.BookingStatus,

                    sh.ShowID,
                    sh.ShowDate,
                    sh.ShowTime,

                    m.MovieID,
                    m.Title AS MovieTitle,

                    t.TheatreID,
                    t.Name AS TheatreName,
                    t.City,

                    sc.ScreenID,
                    sc.ScreenName,
                    sc.ScreenType

                FROM Booking b

                JOIN BookedSeats bs
                    ON b.BookingID = bs.BookingID

                JOIN `Show` sh
                    ON bs.ShowID = sh.ShowID

                JOIN Movie m
                    ON sh.MovieID = m.MovieID

                JOIN Screen sc
                    ON bs.ScreenID = sc.ScreenID

                JOIN Theatre t
                    ON sc.TheatreID = t.TheatreID

                WHERE b.BookingID = ?

                LIMIT 1
                """;

        List<BookingDetailsData> results =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) ->
                                new BookingDetailsData(
                                        rs.getInt("BookingID"),
                                        rs.getLong("TotalAmount"),
                                        rs.getInt("TotalSeatsCount"),
                                        rs.getString("BookingStatus"),

                                        rs.getInt("ShowID"),
                                        rs.getDate("ShowDate")
                                                .toLocalDate(),
                                        rs.getTime("ShowTime")
                                                .toLocalTime(),

                                        rs.getInt("MovieID"),
                                        rs.getString("MovieTitle"),

                                        rs.getInt("TheatreID"),
                                        rs.getString("TheatreName"),
                                        rs.getString("City"),

                                        rs.getInt("ScreenID"),
                                        rs.getString("ScreenName"),
                                        rs.getString("ScreenType")
                                ),
                        bookingId
                );

        if (results.isEmpty()) {
            return null;
        }

        return results.get(0);
    }
}