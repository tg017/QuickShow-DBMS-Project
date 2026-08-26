package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.repository.data.SelectedSeatData;

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
    // CREATE BOOKED SEAT RECORDS
    // =========================================================

    public void createBookedSeats(
            Integer bookingId,
            Integer showId,
            Integer screenId,
            List<Integer> seatIds
    ) {

        String sql = """
                INSERT INTO BookedSeats
                (
                    BookingID,
                    ShowID,
                    ScreenID,
                    SeatID
                )
                VALUES (?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(
                sql,
                seatIds,
                seatIds.size(),
                (statement, seatId) -> {

                    statement.setInt(
                            1,
                            bookingId
                    );

                    statement.setInt(
                            2,
                            showId
                    );

                    statement.setInt(
                            3,
                            screenId
                    );

                    statement.setInt(
                            4,
                            seatId
                    );
                }
        );
    }


    // =========================================================
    // GET SELECTED SEATS FOR BOOKING DETAILS
    // =========================================================

    public List<SelectedSeatData> findSeatsByBookingId(
            Integer bookingId
    ) {

        String sql = """
                SELECT
                    bs.SeatID,
                    s.RowNo,
                    s.SeatNo

                FROM BookedSeats bs

                JOIN Seat s
                    ON bs.ScreenID = s.ScreenID
                   AND bs.SeatID = s.SeatID

                WHERE bs.BookingID = ?

                ORDER BY
                    s.RowNo,
                    s.SeatNo
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        new SelectedSeatData(
                                rs.getInt("SeatID"),
                                rs.getString("RowNo"),
                                rs.getInt("SeatNo")
                        ),
                bookingId
        );
    }
}