package quickshow.dbms.project.repository;

import org.springframework.stereotype.Repository;
import quickshow.dbms.project.dto.MovieShowWithTheatreDTO;
import org.springframework.jdbc.core.JdbcTemplate;

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
}
