package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.PaymentMethod;
import quickshow.dbms.project.model.PaymentStatus;
import quickshow.dbms.project.repository.data.PaymentData;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CREATE PAYMENT
    // =========================================================

    public Integer createPayment(
            Integer bookingId,
            PaymentMethod paymentMethod,
            String transactionId,
            Long paymentAmount,
            PaymentStatus paymentStatus
    ) {

        String sql = """
                INSERT INTO Payment
                (
                    PaymentMethod,
                    TransactionID,
                    PaymentAmount,
                    PaymentDateTime,
                    PaymentStatus,
                    BookingID
                )
                VALUES
                (?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder =
                new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            statement.setString(
                    1,
                    paymentMethod.name()
            );

            statement.setString(
                    2,
                    transactionId
            );

            statement.setLong(
                    3,
                    paymentAmount
            );

            statement.setObject(
                    4,
                    LocalDateTime.now()
            );

            statement.setString(
                    5,
                    paymentStatus.name()
            );

            statement.setInt(
                    6,
                    bookingId
            );

            return statement;

        }, keyHolder);

        Number generatedId =
                keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException(
                    "PaymentID was not generated"
            );
        }

        return generatedId.intValue();
    }


    // =========================================================
    // GET PAYMENT FOR BOOKING
    // =========================================================

    public PaymentData findPaymentByBookingId(
            Integer bookingId
    ) {

        String sql = """
                SELECT
                    PaymentID,
                    PaymentMethod,
                    PaymentAmount,
                    PaymentStatus

                FROM Payment

                WHERE BookingID = ?

                ORDER BY PaymentID DESC

                LIMIT 1
                """;

        List<PaymentData> results =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) ->
                                new PaymentData(
                                        rs.getInt("PaymentID"),
                                        rs.getString("PaymentMethod"),
                                        rs.getLong("PaymentAmount"),
                                        rs.getString("PaymentStatus")
                                ),
                        bookingId
                );

        if (results.isEmpty()) {
            return null;
        }

        return results.get(0);
    }
}