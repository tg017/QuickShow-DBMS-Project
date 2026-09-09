package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import quickshow.dbms.project.dto.AdminPaymentDTO;

import java.util.List;

@Repository
public class AdminPaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminPaymentRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // GET ALL PAYMENTS
    // =========================================================

    public List<AdminPaymentDTO> findAll() {

        String sql = """
                SELECT
                    p.PaymentID,
                    p.PaymentMethod,
                    p.PaymentAmount,
                    p.TransactionID,
                    p.PaymentDateTime,
                    p.PaymentStatus

                FROM Payment p

                ORDER BY
                    p.PaymentDateTime DESC,
                    p.PaymentID DESC
                """;

        return jdbcTemplate.query(
                sql,
                new AdminPaymentRowMapper()
        );
    }


    // =========================================================
    // GET PAYMENT BY ID
    // =========================================================

    public AdminPaymentDTO findById(
            Integer paymentId
    ) {

        String sql = """
                SELECT
                    p.PaymentID,
                    p.PaymentMethod,
                    p.PaymentAmount,
                    p.TransactionID,
                    p.PaymentDateTime,
                    p.PaymentStatus

                FROM Payment p

                WHERE p.PaymentID = ?
                """;

        List<AdminPaymentDTO> result =
                jdbcTemplate.query(
                        sql,
                        new AdminPaymentRowMapper(),
                        paymentId
                );

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }


    // =========================================================
    // CHECK PAYMENT EXISTS
    // =========================================================

    public boolean existsById(
            Integer paymentId
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM Payment
                WHERE PaymentID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        paymentId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // FILTER BY THEATRE
    // =========================================================
    //
    // theatreId is used as the filter value.
    //
    // Payment
    //   -> Booking
    //   -> BookedSeats
    //   -> Show
    //   -> Screen
    //   -> Theatre
    //
    // DISTINCT is important because one booking can contain
    // multiple BookedSeats. Without DISTINCT, the same payment
    // could appear multiple times.
    //
    // =========================================================

    public List<AdminPaymentDTO> findByTheatre(
            Integer theatreId
    ) {

        String sql = """
                SELECT DISTINCT
                    p.PaymentID,
                    p.PaymentMethod,
                    p.PaymentAmount,
                    p.TransactionID,
                    p.PaymentDateTime,
                    p.PaymentStatus

                FROM Payment p

                JOIN Booking b
                    ON p.BookingID = b.BookingID

                JOIN BookedSeats bs
                    ON b.BookingID = bs.BookingID

                JOIN `Show` sh
                    ON bs.ShowID = sh.ShowID

                JOIN Screen sc
                    ON bs.ScreenID = sc.ScreenID

                JOIN Theatre t
                    ON sc.TheatreID = t.TheatreID

                WHERE t.TheatreID = ?

                ORDER BY
                    p.PaymentDateTime DESC,
                    p.PaymentID DESC
                """;

        return jdbcTemplate.query(
                sql,
                new AdminPaymentRowMapper(),
                theatreId
        );
    }


    // =========================================================
    // FILTER BY PAYMENT STATUS
    // =========================================================

    public List<AdminPaymentDTO> findByStatus(
            String paymentStatus
    ) {

        String sql = """
                SELECT
                    p.PaymentID,
                    p.PaymentMethod,
                    p.PaymentAmount,
                    p.TransactionID,
                    p.PaymentDateTime,
                    p.PaymentStatus

                FROM Payment p

                WHERE p.PaymentStatus = ?

                ORDER BY
                    p.PaymentDateTime DESC,
                    p.PaymentID DESC
                """;

        return jdbcTemplate.query(
                sql,
                new AdminPaymentRowMapper(),
                paymentStatus
        );
    }


    // =========================================================
    // FILTER BY THEATRE + PAYMENT STATUS
    // =========================================================

    public List<AdminPaymentDTO> findByTheatreAndStatus(
            Integer theatreId,
            String paymentStatus
    ) {

        String sql = """
                SELECT DISTINCT
                    p.PaymentID,
                    p.PaymentMethod,
                    p.PaymentAmount,
                    p.TransactionID,
                    p.PaymentDateTime,
                    p.PaymentStatus

                FROM Payment p

                JOIN Booking b
                    ON p.BookingID = b.BookingID

                JOIN BookedSeats bs
                    ON b.BookingID = bs.BookingID

                JOIN `Show` sh
                    ON bs.ShowID = sh.ShowID

                JOIN Screen sc
                    ON bs.ScreenID = sc.ScreenID

                JOIN Theatre t
                    ON sc.TheatreID = t.TheatreID

                WHERE t.TheatreID = ?
                  AND p.PaymentStatus = ?

                ORDER BY
                    p.PaymentDateTime DESC,
                    p.PaymentID DESC
                """;

        return jdbcTemplate.query(
                sql,
                new AdminPaymentRowMapper(),
                theatreId,
                paymentStatus
        );
    }
}