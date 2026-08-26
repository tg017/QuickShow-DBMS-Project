package quickshow.dbms.project.repository;

import org.springframework.stereotype.Repository;
import quickshow.dbms.project.dto.MovieShowWithTheatreDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import quickshow.dbms.project.dto.SeatDTO;
import quickshow.dbms.project.dto.SeatLayoutDTO;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ShowRepository {

    private final JdbcTemplate jdbcTemplate;

    public ShowRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MovieShowWithTheatreDTO> findShowsByMovieAndDate(
            Integer movieId,
            LocalDate date
    ) {

        String sql = """
            SELECT
                t.TheatreID,
                t.Name AS TheatreName,
                sh.ShowID,
                m.Title AS MovieTitle,
                m.Language,
                sc.ScreenType,
                sh.AvailableSeats,
                sh.TicketPrice,
                sh.ShowTime
            FROM `Show` sh
            JOIN Screen sc
                ON sh.ScreenID = sc.ScreenID
            JOIN Theatre t
                ON sc.TheatreID = t.TheatreID
            JOIN Movie m
                ON sh.MovieID = m.MovieID
            WHERE sh.MovieID = ?
              AND sh.ShowDate = ?
              AND sh.ShowStatus = 'SCHEDULED'
            ORDER BY t.TheatreID, sh.ShowTime
            """;

        return jdbcTemplate.query(
                sql,
                new MovieShowWithTheatreRowMapper(),
                movieId,
                date
        );
    }

    // =========================================================
    // GET SHOW DETAILS FOR SEAT LAYOUT
    // =========================================================

    public SeatLayoutDTO findSeatLayoutShow(
            Integer showId
    ) {

        String sql = """
                SELECT
                    sh.ShowID,
                    sh.ShowDate,
                    sh.ShowTime,
                    sh.TicketPrice,
                    sh.AvailableSeats,
                    sh.ShowStatus,

                    m.MovieID,
                    m.Title AS MovieTitle,
                    m.Poster,

                    t.TheatreID,
                    t.Name AS TheatreName,
                    t.City,

                    sc.ScreenID,
                    sc.ScreenName,
                    sc.ScreenType

                FROM `Show` sh

                JOIN Movie m
                    ON sh.MovieID = m.MovieID

                JOIN Screen sc
                    ON sh.ScreenID = sc.ScreenID

                JOIN Theatre t
                    ON sc.TheatreID = t.TheatreID

                WHERE sh.ShowID = ?
                """;

        List<SeatLayoutDTO> results =
                jdbcTemplate.query(
                        sql,
                        new SeatLayoutShowRowMapper(),
                        showId
                );

        if (results.isEmpty()) {
            return null;
        }

        return results.get(0);
    }


    // =========================================================
    // GET SEATS FOR SHOW
    // =========================================================

    public List<SeatDTO> findSeatsByShowId(
            Integer showId
    ) {

        String sql = """
                SELECT
                    s.SeatID,
                    s.RowNo,
                    s.SeatNo,
                    ssa.Status

                FROM ShowSeatAllocates ssa

                JOIN Seat s
                    ON ssa.ScreenID = s.ScreenID
                   AND ssa.SeatID = s.SeatID

                WHERE ssa.ShowID = ?

                ORDER BY
                    s.RowNo,
                    s.SeatNo
                """;

        return jdbcTemplate.query(
                sql,
                new SeatRowMapper(),
                showId
        );
    }
}
