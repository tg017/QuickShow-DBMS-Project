package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import quickshow.dbms.project.dto.AdminScreenDTO;
import quickshow.dbms.project.dto.SeatRowConfigDTO;

import java.util.List;

@Repository
public class AdminScreenRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminScreenRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // GET ALL SCREENS OF THEATRE
    // =========================================================

    public List<AdminScreenDTO> findByTheatreId(
            Integer theatreId
    ) {

        String sql = """
                SELECT
                    ScreenID,
                    ScreenName,
                    ScreenType,
                    SeatingCapacity,
                    TheatreID
                FROM Screen
                WHERE TheatreID = ?
                ORDER BY ScreenID
                """;

        return jdbcTemplate.query(
                sql,
                new AdminScreenRowMapper(),
                theatreId
        );
    }


    // =========================================================
    // GET SCREEN BY ID
    // =========================================================

    public AdminScreenDTO findById(
            Integer screenId
    ) {

        String sql = """
                SELECT
                    ScreenID,
                    ScreenName,
                    ScreenType,
                    SeatingCapacity,
                    TheatreID
                FROM Screen
                WHERE ScreenID = ?
                """;

        List<AdminScreenDTO> result =
                jdbcTemplate.query(
                        sql,
                        new AdminScreenRowMapper(),
                        screenId
                );

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }


    // =========================================================
    // CREATE SCREEN
    // =========================================================

    public Integer createScreen(
            Integer theatreId,
            AdminScreenDTO screen
    ) {

        String sql = """
                INSERT INTO Screen
                (
                    ScreenName,
                    ScreenType,
                    SeatingCapacity,
                    TheatreID
                )
                VALUES (?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                screen.getName(),
                screen.getScreenType().name(),
                screen.getCapacity(),
                theatreId
        );

        return jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Integer.class
        );
    }


    // =========================================================
    // CREATE SEAT
    // =========================================================

    public void createSeat(
            Integer screenId,
            Integer seatId,
            String rowNo,
            Integer seatNo
    ) {

        String sql = """
                INSERT INTO Seat
                (
                    ScreenID,
                    SeatID,
                    RowNo,
                    SeatNo
                )
                VALUES (?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                screenId,
                seatId,
                rowNo,
                seatNo
        );
    }


    // =========================================================
    // CREATE ALL SEATS FOR SCREEN
    // =========================================================

    public void createSeats(
            Integer screenId,
            List<SeatRowConfigDTO> rows
    ) {

        String sql = """
            INSERT INTO Seat
            (
                ScreenID,
                SeatID,
                RowNo,
                SeatNo
            )
            VALUES (?, ?, ?, ?)
            """;

        int seatId = 1;

        for (SeatRowConfigDTO row : rows) {

            for (int seatNo = 1;
                 seatNo <= row.getSeatCount();
                 seatNo++) {

                jdbcTemplate.update(
                        sql,
                        screenId,
                        seatId,
                        row.getRowNo(),
                        seatNo
                );

                seatId++;
            }
        }
    }


    // =========================================================
    // EXISTS
    // =========================================================

    public boolean existsById(
            Integer screenId
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM Screen
                WHERE ScreenID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        screenId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // HAS SEATS
    // =========================================================

    public boolean hasSeats(
            Integer screenId
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM Seat
                WHERE ScreenID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        screenId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // HAS SHOWS
    // =========================================================

    public boolean hasShows(
            Integer screenId
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM `Show`
                WHERE ScreenID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        screenId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // UPDATE SCREEN
    // =========================================================

    public int update(
            Integer screenId,
            AdminScreenDTO screen
    ) {

        String sql = """
                UPDATE Screen
                SET
                    ScreenName = ?,
                    ScreenType = ?,
                    SeatingCapacity = ?
                WHERE ScreenID = ?
                """;

        return jdbcTemplate.update(
                sql,
                screen.getName(),
                screen.getScreenType().name(),
                screen.getCapacity(),
                screenId
        );
    }


    // =========================================================
    // DELETE SCREEN
    // =========================================================

    public int delete(
            Integer screenId
    ) {

        String sql = """
                DELETE FROM Screen
                WHERE ScreenID = ?
                """;

        return jdbcTemplate.update(
                sql,
                screenId
        );
    }
}