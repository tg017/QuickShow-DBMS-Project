package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.Booking;
import quickshow.dbms.project.model.BookingStatus;

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
    // CREATE
    // =========================================================

    public Booking create(Booking booking) {

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
                    (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder =
                new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            statement.setInt(
                    1,
                    booking.getCustomer()
                            .getUserId()
            );

            statement.setObject(
                    2,
                    booking.getBookingDateTime()
            );

            statement.setString(
                    3,
                    booking.getBookingStatus()
                            .name()
            );

            statement.setLong(
                    4,
                    booking.getTotalAmount()
            );

            statement.setInt(
                    5,
                    booking.getTotalSeatsCount()
            );

            return statement;

        }, keyHolder);

        Number generatedId =
                keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException(
                    "BookingID was not generated"
            );
        }

        booking.setBookingId(
                generatedId.intValue()
        );

        return booking;
    }


    // =========================================================
    // READ BY ID
    // =========================================================

    public Booking findById(
            Integer bookingId
    ) {

        String sql = """
                SELECT
                    BookingID,
                    UserID,
                    BookingDateTime,
                    BookingStatus,
                    TotalAmount,
                    TotalSeatsCount
                FROM Booking
                WHERE BookingID = ?
                """;

        List<Booking> bookings =
                jdbcTemplate.query(
                        sql,
                        new BookingRowMapper(),
                        bookingId
                );

        if (bookings.isEmpty()) {
            return null;
        }

        return bookings.get(0);
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<Booking> findAll() {

        String sql = """
                SELECT
                    BookingID,
                    UserID,
                    BookingDateTime,
                    BookingStatus,
                    TotalAmount,
                    TotalSeatsCount
                FROM Booking
                ORDER BY BookingID
                """;

        return jdbcTemplate.query(
                sql,
                new BookingRowMapper()
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    public int update(Booking booking) {

        String sql = """
                UPDATE Booking
                SET
                    UserID = ?,
                    BookingDateTime = ?,
                    BookingStatus = ?,
                    TotalAmount = ?,
                    TotalSeatsCount = ?
                WHERE BookingID = ?
                """;

        return jdbcTemplate.update(
                sql,
                booking.getCustomer()
                        .getUserId(),
                booking.getBookingDateTime(),
                booking.getBookingStatus()
                        .name(),
                booking.getTotalAmount(),
                booking.getTotalSeatsCount(),
                booking.getBookingId()
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    public int deleteById(
            Integer bookingId
    ) {

        String sql = """
                DELETE FROM Booking
                WHERE BookingID = ?
                """;

        return jdbcTemplate.update(
                sql,
                bookingId
        );
    }


    // =========================================================
    // EXISTS
    // =========================================================

    public boolean existsById(
            Integer bookingId
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM Booking
                WHERE BookingID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        bookingId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // FIND BY CUSTOMER
    // =========================================================

    public List<Booking> findByCustomerId(
            Integer userId
    ) {

        String sql = """
                SELECT
                    BookingID,
                    UserID,
                    BookingDateTime,
                    BookingStatus,
                    TotalAmount,
                    TotalSeatsCount
                FROM Booking
                WHERE UserID = ?
                ORDER BY BookingID
                """;

        return jdbcTemplate.query(
                sql,
                new BookingRowMapper(),
                userId
        );
    }


    // =========================================================
    // FIND BY STATUS
    // =========================================================

    public List<Booking> findByStatus(
            BookingStatus status
    ) {

        String sql = """
                SELECT
                    BookingID,
                    UserID,
                    BookingDateTime,
                    BookingStatus,
                    TotalAmount,
                    TotalSeatsCount
                FROM Booking
                WHERE BookingStatus = ?
                ORDER BY BookingID
                """;

        return jdbcTemplate.query(
                sql,
                new BookingRowMapper(),
                status.name()
        );
    }


    // =========================================================
    // FIND BY CUSTOMER AND STATUS
    // =========================================================

    public List<Booking> findByCustomerIdAndStatus(
            Integer userId,
            BookingStatus status
    ) {

        String sql = """
                SELECT
                    BookingID,
                    UserID,
                    BookingDateTime,
                    BookingStatus,
                    TotalAmount,
                    TotalSeatsCount
                FROM Booking
                WHERE UserID = ?
                  AND BookingStatus = ?
                ORDER BY BookingID
                """;

        return jdbcTemplate.query(
                sql,
                new BookingRowMapper(),
                userId,
                status.name()
        );
    }
}