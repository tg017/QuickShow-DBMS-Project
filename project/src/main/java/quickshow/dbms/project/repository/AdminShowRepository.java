package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import quickshow.dbms.project.dto.AdminShowDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class AdminShowRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminShowRepository(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // GET ALL SHOWS
    // =========================================================

    public List<AdminShowDTO> findAll() {

        String sql = """
                SELECT
                    sh.ShowID,
                    sh.ShowDate,
                    sh.ShowTime,
                    sh.TicketPrice,

                    (
                        SELECT COUNT(*)
                        FROM ShowSeatAllocates ssa
                        WHERE ssa.ShowID = sh.ShowID
                          AND ssa.Status = 'AVAILABLE'
                    ) AS AvailableSeats,

                    sh.ShowStatus,
                    sh.MovieID,
                    sh.ScreenID

                FROM `Show` sh

                ORDER BY
                    sh.ShowDate,
                    sh.ShowTime,
                    sh.ShowID
                """;

        return jdbcTemplate.query(
                sql,
                new AdminShowRowMapper()
        );
    }


    // =========================================================
    // GET SHOW BY ID
    // =========================================================

    public AdminShowDTO findById(
            Integer showId
    ) {

        String sql = """
                SELECT
                    sh.ShowID,
                    sh.ShowDate,
                    sh.ShowTime,
                    sh.TicketPrice,

                    (
                        SELECT COUNT(*)
                        FROM ShowSeatAllocates ssa
                        WHERE ssa.ShowID = sh.ShowID
                          AND ssa.Status = 'AVAILABLE'
                    ) AS AvailableSeats,

                    sh.ShowStatus,
                    sh.MovieID,
                    sh.ScreenID

                FROM `Show` sh

                WHERE sh.ShowID = ?
                """;

        List<AdminShowDTO> shows =
                jdbcTemplate.query(
                        sql,
                        new AdminShowRowMapper(),
                        showId
                );

        if (shows.isEmpty()) {
            return null;
        }

        return shows.get(0);
    }


    // =========================================================
    // CHECK SHOW EXISTS
    // =========================================================

    public boolean existsById(
            Integer showId
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM `Show`
                WHERE ShowID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        showId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // CHECK MOVIE EXISTS
    // =========================================================

    public boolean movieExists(
            Integer movieId
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM Movie
                WHERE MovieID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        movieId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // CHECK SCREEN EXISTS
    // =========================================================

    public boolean screenExists(
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
    // CHECK SCREEN HAS SEATS
    // =========================================================

    public boolean screenHasSeats(
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
    // CHECK SHOW TIME CONFLICT
    // =========================================================

    public boolean showExistsAtTime(
            Integer screenId,
            Integer movieId,
            LocalDate showDate,
            LocalTime showTime,
            Integer excludeShowId
    ) {

        String sql = """
            SELECT COUNT(*)
            FROM `Show` sh
            JOIN Movie m
                ON sh.MovieID = m.MovieID
            WHERE sh.ScreenID = ?
              AND sh.ShowDate = ?
              AND sh.ShowID <> ?

              AND
              (
                  /*
                   * Existing show starts before the new show
                   * finishes + 30 minutes
                   */
                  TIMESTAMP(sh.ShowDate, sh.ShowTime)
                      <
                  DATE_ADD(
                      TIMESTAMP(?, ?),
                      INTERVAL (
                          (
                              SELECT Duration
                              FROM Movie
                              WHERE MovieID = ?
                          )
                          + 30
                      ) MINUTE
                  )

                  AND

                  /*
                   * New show starts before the existing show
                   * finishes + 30 minutes
                   */
                  TIMESTAMP(?, ?)
                      <
                  DATE_ADD(
                      TIMESTAMP(sh.ShowDate, sh.ShowTime),
                      INTERVAL (m.Duration + 30) MINUTE
                  )
              )
            """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,

                        screenId,
                        showDate,
                        excludeShowId == null
                                ? -1
                                : excludeShowId,

                        showDate,
                        showTime,
                        movieId,

                        showDate,
                        showTime
                );

        return count != null && count > 0;
    }


    // =========================================================
    // CHECK WHETHER SHOW HAS BOOKED SEATS
    // =========================================================
    //
    // This is the important constraint from your diagram.
    //
    // We check ShowSeatAllocates rather than simply looking
    // at Booking records.
    //
    // If an allocation belonging to this show is BOOKED,
    // the show has an actual booked seat.
    //
    // =========================================================

    public boolean hasBookedSeats(
            Integer showId
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM ShowSeatAllocates
                WHERE ShowID = ?
                  AND Status = 'BOOKED'
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        showId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // CHECK WHETHER BOOKED-SEAT RECORDS EXIST
    // =========================================================
    //
    // This is an additional protection because BookedSeats
    // references ShowSeatAllocates directly.
    //
    // It prevents us from deleting an allocation that is
    // still referenced by booking history.
    //
    // =========================================================

    public boolean hasBookedSeatRecords(
            Integer showId
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM BookedSeats
                WHERE ShowID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        showId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // CREATE SHOW
    // =========================================================

    public Integer create(
            AdminShowDTO show
    ) {

        String sql = """
                INSERT INTO `Show`
                (
                    ShowDate,
                    ShowTime,
                    TicketPrice,
                    AvailableSeats,
                    ShowStatus,
                    MovieID,
                    ScreenID
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                show.getShowDate(),
                show.getShowTime(),
                show.getTicketPrice(),

                // Will be synchronized after allocations
                0,

                show.getShowStatus().name(),
                show.getMovieId(),
                show.getScreenId()
        );

        return jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Integer.class
        );
    }


    // =========================================================
    // UPDATE SHOW
    // =========================================================

    public int update(
            Integer showId,
            AdminShowDTO show
    ) {

        String sql = """
                UPDATE `Show`
                SET
                    ShowDate = ?,
                    ShowTime = ?,
                    TicketPrice = ?,
                    ShowStatus = ?,
                    MovieID = ?,
                    ScreenID = ?
                WHERE ShowID = ?
                """;

        return jdbcTemplate.update(
                sql,
                show.getShowDate(),
                show.getShowTime(),
                show.getTicketPrice(),
                show.getShowStatus().name(),
                show.getMovieId(),
                show.getScreenId(),
                showId
        );
    }


    // =========================================================
    // UPDATE SHOW WITHOUT CHANGING SCREEN
    // =========================================================

    public int updateWithoutScreen(
            Integer showId,
            AdminShowDTO show
    ) {

        String sql = """
                UPDATE `Show`
                SET
                    ShowDate = ?,
                    ShowTime = ?,
                    TicketPrice = ?,
                    ShowStatus = ?,
                    MovieID = ?
                WHERE ShowID = ?
                """;

        return jdbcTemplate.update(
                sql,
                show.getShowDate(),
                show.getShowTime(),
                show.getTicketPrice(),
                show.getShowStatus().name(),
                show.getMovieId(),
                showId
        );
    }


    // =========================================================
    // UPDATE AVAILABLE SEATS
    // =========================================================

    public int updateAvailableSeats(
            Integer showId,
            Integer availableSeats
    ) {

        String sql = """
                UPDATE `Show`
                SET AvailableSeats = ?
                WHERE ShowID = ?
                """;

        return jdbcTemplate.update(
                sql,
                availableSeats,
                showId
        );
    }


    // =========================================================
    // DELETE SHOW
    // =========================================================

    public int delete(
            Integer showId
    ) {

        String sql = """
                DELETE FROM `Show`
                WHERE ShowID = ?
                """;

        return jdbcTemplate.update(
                sql,
                showId
        );
    }
}