package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.Seat;
import quickshow.dbms.project.model.SeatId;

import java.util.List;

@Repository
public class SeatRepository {

    private final JdbcTemplate jdbcTemplate;

    public SeatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CREATE
    // =========================================================

    public Seat create(Seat seat) {

        String sql = """
                INSERT INTO Seat
                    (
                        ScreenID,
                        SeatID,
                        RowNo,
                        SeatNo
                    )
                VALUES
                    (?, ?, ?, ?)
                """;

        SeatId id = seat.getId();

        jdbcTemplate.update(
                sql,
                id.getScreenId(),
                id.getSeatId(),
                seat.getRowNo(),
                seat.getSeatNo()
        );

        return seat;
    }


    // =========================================================
    // READ BY COMPOSITE ID
    // =========================================================

    public Seat findById(SeatId id) {

        String sql = """
                SELECT
                    ScreenID,
                    SeatID,
                    RowNo,
                    SeatNo
                FROM Seat
                WHERE ScreenID = ?
                  AND SeatID = ?
                """;

        List<Seat> seats =
                jdbcTemplate.query(
                        sql,
                        new SeatRowMapper(),
                        id.getScreenId(),
                        id.getSeatId()
                );

        if (seats.isEmpty()) {
            return null;
        }

        return seats.get(0);
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<Seat> findAll() {

        String sql = """
                SELECT
                    ScreenID,
                    SeatID,
                    RowNo,
                    SeatNo
                FROM Seat
                ORDER BY ScreenID, SeatID
                """;

        return jdbcTemplate.query(
                sql,
                new SeatRowMapper()
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    public int update(Seat seat) {

        String sql = """
                UPDATE Seat
                SET
                    RowNo = ?,
                    SeatNo = ?
                WHERE ScreenID = ?
                  AND SeatID = ?
                """;

        SeatId id = seat.getId();

        return jdbcTemplate.update(
                sql,
                seat.getRowNo(),
                seat.getSeatNo(),
                id.getScreenId(),
                id.getSeatId()
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    public int deleteById(SeatId id) {

        String sql = """
                DELETE FROM Seat
                WHERE ScreenID = ?
                  AND SeatID = ?
                """;

        return jdbcTemplate.update(
                sql,
                id.getScreenId(),
                id.getSeatId()
        );
    }


    // =========================================================
    // EXISTS
    // =========================================================

    public boolean existsById(SeatId id) {

        String sql = """
                SELECT COUNT(*)
                FROM Seat
                WHERE ScreenID = ?
                  AND SeatID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        id.getScreenId(),
                        id.getSeatId()
                );

        return count != null && count > 0;
    }


    // =========================================================
    // FIND BY SCREEN
    // =========================================================

    public List<Seat> findByScreen(
            Integer screenId
    ) {

        String sql = """
                SELECT
                    ScreenID,
                    SeatID,
                    RowNo,
                    SeatNo
                FROM Seat
                WHERE ScreenID = ?
                ORDER BY RowNo, SeatNo
                """;

        return jdbcTemplate.query(
                sql,
                new SeatRowMapper(),
                screenId
        );
    }


    // =========================================================
    // FIND BY SCREEN AND ROW
    // =========================================================

    public List<Seat> findByScreenAndRow(
            Integer screenId,
            String rowNo
    ) {

        String sql = """
                SELECT
                    ScreenID,
                    SeatID,
                    RowNo,
                    SeatNo
                FROM Seat
                WHERE ScreenID = ?
                  AND RowNo = ?
                ORDER BY SeatNo
                """;

        return jdbcTemplate.query(
                sql,
                new SeatRowMapper(),
                screenId,
                rowNo
        );
    }
}