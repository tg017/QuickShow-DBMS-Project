package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.Show;
import quickshow.dbms.project.model.ShowStatus;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ShowRepository {

    private final JdbcTemplate jdbcTemplate;

    public ShowRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CREATE
    // =========================================================

    public Show create(
            Show show,
            Integer movieId,
            Integer screenId
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
                VALUES
                    (?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder =
                new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            statement.setObject(
                    1,
                    show.getShowDate()
            );

            statement.setObject(
                    2,
                    show.getShowTime()
            );

            statement.setObject(
                    3,
                    show.getTicketPrice()
            );

            statement.setObject(
                    4,
                    show.getAvailableSeats()
            );

            statement.setString(
                    5,
                    show.getShowStatus().name()
            );

            statement.setInt(
                    6,
                    movieId
            );

            statement.setInt(
                    7,
                    screenId
            );

            return statement;

        }, keyHolder);

        Number generatedId =
                keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException(
                    "ShowID was not generated"
            );
        }

        show.setShowId(
                generatedId.intValue()
        );

        return show;
    }


    // =========================================================
    // READ BY ID
    // =========================================================

    public Show findById(
            Integer showId
    ) {

        String sql = """
                SELECT
                    ShowID,
                    ShowDate,
                    ShowTime,
                    TicketPrice,
                    AvailableSeats,
                    ShowStatus
                FROM `Show`
                WHERE ShowID = ?
                """;

        List<Show> shows =
                jdbcTemplate.query(
                        sql,
                        new ShowRowMapper(),
                        showId
                );

        if (shows.isEmpty()) {
            return null;
        }

        return shows.get(0);
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<Show> findAll() {

        String sql = """
                SELECT
                    ShowID,
                    ShowDate,
                    ShowTime,
                    TicketPrice,
                    AvailableSeats,
                    ShowStatus
                FROM `Show`
                ORDER BY ShowID
                """;

        return jdbcTemplate.query(
                sql,
                new ShowRowMapper()
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    public int update(
            Show show
    ) {

        String sql = """
                UPDATE `Show`
                SET
                    ShowDate = ?,
                    ShowTime = ?,
                    TicketPrice = ?,
                    AvailableSeats = ?,
                    ShowStatus = ?
                WHERE ShowID = ?
                """;

        return jdbcTemplate.update(
                sql,
                show.getShowDate(),
                show.getShowTime(),
                show.getTicketPrice(),
                show.getAvailableSeats(),
                show.getShowStatus().name(),
                show.getShowId()
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    public int deleteById(
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


    // =========================================================
    // EXISTS
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
    // FIND BY MOVIE
    // =========================================================

    public List<Show> findByMovie(
            Integer movieId
    ) {

        String sql = """
                SELECT
                    ShowID,
                    ShowDate,
                    ShowTime,
                    TicketPrice,
                    AvailableSeats,
                    ShowStatus
                FROM `Show`
                WHERE MovieID = ?
                ORDER BY ShowDate, ShowTime
                """;

        return jdbcTemplate.query(
                sql,
                new ShowRowMapper(),
                movieId
        );
    }


    // =========================================================
    // FIND BY SCREEN
    // =========================================================

    public List<Show> findByScreen(
            Integer screenId
    ) {

        String sql = """
                SELECT
                    ShowID,
                    ShowDate,
                    ShowTime,
                    TicketPrice,
                    AvailableSeats,
                    ShowStatus
                FROM `Show`
                WHERE ScreenID = ?
                ORDER BY ShowDate, ShowTime
                """;

        return jdbcTemplate.query(
                sql,
                new ShowRowMapper(),
                screenId
        );
    }


    // =========================================================
    // FIND BY STATUS
    // =========================================================

    public List<Show> findByStatus(
            ShowStatus status
    ) {

        String sql = """
                SELECT
                    ShowID,
                    ShowDate,
                    ShowTime,
                    TicketPrice,
                    AvailableSeats,
                    ShowStatus
                FROM `Show`
                WHERE ShowStatus = ?
                ORDER BY ShowDate, ShowTime
                """;

        return jdbcTemplate.query(
                sql,
                new ShowRowMapper(),
                status.name()
        );
    }


    // =========================================================
    // FIND BY SCREEN AND DATE
    // =========================================================

    public List<Show> findByScreenAndDate(
            Integer screenId,
            LocalDate showDate
    ) {

        String sql = """
                SELECT
                    ShowID,
                    ShowDate,
                    ShowTime,
                    TicketPrice,
                    AvailableSeats,
                    ShowStatus
                FROM `Show`
                WHERE ScreenID = ?
                  AND ShowDate = ?
                ORDER BY ShowTime
                """;

        return jdbcTemplate.query(
                sql,
                new ShowRowMapper(),
                screenId,
                showDate
        );
    }
}