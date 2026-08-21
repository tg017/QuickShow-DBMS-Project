package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.SeatAllocationStatus;
import quickshow.dbms.project.model.ShowSeatAllocation;
import quickshow.dbms.project.model.ShowSeatAllocationId;

import java.util.List;

@Repository
public class ShowSeatAllocationRepository {

    private final JdbcTemplate jdbcTemplate;

    public ShowSeatAllocationRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CREATE
    // =========================================================

    public ShowSeatAllocation create(
            ShowSeatAllocation allocation
    ) {

        String sql = """
                INSERT INTO ShowSeatAllocates
                    (
                        ShowID,
                        SeatID,
                        ScreenID,
                        Status
                    )
                VALUES
                    (?, ?, ?, ?)
                """;

        ShowSeatAllocationId id =
                allocation.getId();

        jdbcTemplate.update(
                sql,
                id.getShowId(),
                id.getSeatId(),
                id.getScreenId(),
                allocation.getStatus().name()
        );

        return allocation;
    }


    // =========================================================
    // READ BY COMPOSITE ID
    // =========================================================

    public ShowSeatAllocation findById(
            ShowSeatAllocationId id
    ) {

        String sql = """
                SELECT
                    ShowID,
                    SeatID,
                    ScreenID,
                    Status
                FROM ShowSeatAllocates
                WHERE ShowID = ?
                  AND ScreenID = ?
                  AND SeatID = ?
                """;

        List<ShowSeatAllocation> allocations =
                jdbcTemplate.query(
                        sql,
                        new ShowSeatAllocationRowMapper(),
                        id.getShowId(),
                        id.getScreenId(),
                        id.getSeatId()
                );

        if (allocations.isEmpty()) {
            return null;
        }

        return allocations.get(0);
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<ShowSeatAllocation> findAll() {

        String sql = """
                SELECT
                    ShowID,
                    SeatID,
                    ScreenID,
                    Status
                FROM ShowSeatAllocates
                ORDER BY ShowID, ScreenID, SeatID
                """;

        return jdbcTemplate.query(
                sql,
                new ShowSeatAllocationRowMapper()
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    public int update(
            ShowSeatAllocation allocation
    ) {

        String sql = """
                UPDATE ShowSeatAllocates
                SET
                    Status = ?
                WHERE ShowID = ?
                  AND ScreenID = ?
                  AND SeatID = ?
                """;

        ShowSeatAllocationId id =
                allocation.getId();

        return jdbcTemplate.update(
                sql,
                allocation.getStatus().name(),
                id.getShowId(),
                id.getScreenId(),
                id.getSeatId()
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    public int deleteById(
            ShowSeatAllocationId id
    ) {

        String sql = """
                DELETE FROM ShowSeatAllocates
                WHERE ShowID = ?
                  AND ScreenID = ?
                  AND SeatID = ?
                """;

        return jdbcTemplate.update(
                sql,
                id.getShowId(),
                id.getScreenId(),
                id.getSeatId()
        );
    }


    // =========================================================
    // EXISTS
    // =========================================================

    public boolean existsById(
            ShowSeatAllocationId id
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM ShowSeatAllocates
                WHERE ShowID = ?
                  AND ScreenID = ?
                  AND SeatID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        id.getShowId(),
                        id.getScreenId(),
                        id.getSeatId()
                );

        return count != null && count > 0;
    }


    // =========================================================
    // FIND BY SHOW
    // =========================================================

    public List<ShowSeatAllocation> findByShow(
            Integer showId
    ) {

        String sql = """
                SELECT
                    ShowID,
                    SeatID,
                    ScreenID,
                    Status
                FROM ShowSeatAllocates
                WHERE ShowID = ?
                ORDER BY ScreenID, SeatID
                """;

        return jdbcTemplate.query(
                sql,
                new ShowSeatAllocationRowMapper(),
                showId
        );
    }


    // =========================================================
    // FIND BY SEAT
    // =========================================================

    public List<ShowSeatAllocation> findBySeat(
            Integer screenId,
            Integer seatId
    ) {

        String sql = """
                SELECT
                    ShowID,
                    SeatID,
                    ScreenID,
                    Status
                FROM ShowSeatAllocates
                WHERE ScreenID = ?
                  AND SeatID = ?
                ORDER BY ShowID
                """;

        return jdbcTemplate.query(
                sql,
                new ShowSeatAllocationRowMapper(),
                screenId,
                seatId
        );
    }


    // =========================================================
    // FIND BY SHOW AND STATUS
    // =========================================================

    public List<ShowSeatAllocation> findByShowAndStatus(
            Integer showId,
            SeatAllocationStatus status
    ) {

        String sql = """
                SELECT
                    ShowID,
                    SeatID,
                    ScreenID,
                    Status
                FROM ShowSeatAllocates
                WHERE ShowID = ?
                  AND Status = ?
                ORDER BY ScreenID, SeatID
                """;

        return jdbcTemplate.query(
                sql,
                new ShowSeatAllocationRowMapper(),
                showId,
                status.name()
        );
    }


    // =========================================================
    // FIND BY SHOW AND SCREEN
    // =========================================================

    public List<ShowSeatAllocation> findByShowAndScreen(
            Integer showId,
            Integer screenId
    ) {

        String sql = """
                SELECT
                    ShowID,
                    SeatID,
                    ScreenID,
                    Status
                FROM ShowSeatAllocates
                WHERE ShowID = ?
                  AND ScreenID = ?
                ORDER BY SeatID
                """;

        return jdbcTemplate.query(
                sql,
                new ShowSeatAllocationRowMapper(),
                showId,
                screenId
        );
    }
}