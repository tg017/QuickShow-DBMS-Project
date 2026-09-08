package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ShowSeatAllocationRepository {

    private final JdbcTemplate jdbcTemplate;

    public ShowSeatAllocationRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CLAIM SEATS ATOMICALLY
    // =========================================================

    public int claimSeats(
            Integer showId,
            Integer screenId,
            java.util.List<Integer> seatIds
    ) {

        if (seatIds == null || seatIds.isEmpty()) {
            return 0;
        }

        String placeholders =
                String.join(
                        ", ",
                        java.util.Collections.nCopies(
                                seatIds.size(),
                                "?"
                        )
                );

        String sql = """
                UPDATE ShowSeatAllocates
                SET Status = 'BOOKED'
                WHERE ShowID = ?
                  AND ScreenID = ?
                  AND SeatID IN (%s)
                  AND Status = 'AVAILABLE'
                """.formatted(placeholders);

        java.util.List<Object> parameters =
                new java.util.ArrayList<>();

        parameters.add(showId);
        parameters.add(screenId);
        parameters.addAll(seatIds);

        return jdbcTemplate.update(
                sql,
                parameters.toArray()
        );
    }


    // =========================================================
    // RELEASE SEATS
    // =========================================================

    public int releaseSeats(
            Integer showId,
            Integer screenId,
            java.util.List<Integer> seatIds
    ) {

        if (seatIds == null || seatIds.isEmpty()) {
            return 0;
        }

        String placeholders =
                String.join(
                        ", ",
                        java.util.Collections.nCopies(
                                seatIds.size(),
                                "?"
                        )
                );

        String sql = """
                UPDATE ShowSeatAllocates
                SET Status = 'AVAILABLE'
                WHERE ShowID = ?
                  AND ScreenID = ?
                  AND SeatID IN (%s)
                  AND Status = 'BOOKED'
                """.formatted(placeholders);

        java.util.List<Object> parameters =
                new java.util.ArrayList<>();

        parameters.add(showId);
        parameters.add(screenId);
        parameters.addAll(seatIds);

        return jdbcTemplate.update(
                sql,
                parameters.toArray()
        );
    }

    // =========================================================
// CREATE ALLOCATIONS FOR SHOW
// =========================================================

    public int createAllocationsForShow(
            Integer showId,
            Integer screenId
    ) {

        String sql = """
            INSERT INTO ShowSeatAllocates
            (
                ShowID,
                ScreenID,
                SeatID,
                Status
            )
            SELECT
                ?,
                ScreenID,
                SeatID,
                'AVAILABLE'
            FROM Seat
            WHERE ScreenID = ?
            """;

        return jdbcTemplate.update(
                sql,
                showId,
                screenId
        );
    }


// =========================================================
// COUNT AVAILABLE SEATS
// =========================================================

    public Integer countAvailableSeats(
            Integer showId
    ) {

        String sql = """
            SELECT COUNT(*)
            FROM ShowSeatAllocates
            WHERE ShowID = ?
              AND Status = 'AVAILABLE'
            """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        showId
                );

        return count == null ? 0 : count;
    }


// =========================================================
// DELETE ALLOCATIONS FOR SHOW
// =========================================================

    public int deleteAllocationsForShow(
            Integer showId
    ) {

        String sql = """
            DELETE FROM ShowSeatAllocates
            WHERE ShowID = ?
            """;

        return jdbcTemplate.update(
                sql,
                showId
        );
    }
}