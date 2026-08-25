package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.dto.TheatreListDTO;
import quickshow.dbms.project.dto.TheatreShowDTO;

import java.time.LocalDate;
import java.util.List;

@Repository
public class TheatreRepository {

    private final JdbcTemplate jdbcTemplate;

    public TheatreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TheatreListDTO> findTheatresWithScheduledShows() {

        String sql = """
                SELECT
                    t.TheatreID,
                    t.Name,
                    t.BuildingName,
                    t.Street,
                    t.Area,
                    t.City,
                    t.State,
                    t.PinCode
                FROM Theatre t
                WHERE EXISTS (
                    SELECT 1
                    FROM Screen s
                    JOIN `Show` sh
                        ON sh.ScreenID = s.ScreenID
                    WHERE s.TheatreID = t.TheatreID
                      AND sh.ShowStatus = 'SCHEDULED'
                )
                ORDER BY t.TheatreID
                """;

        return jdbcTemplate.query(
                sql,
                new TheatreListRowMapper()
        );
    }

    public List<TheatreListDTO> findTheatresByCity(
            String city
    ) {

        String sql = """
            SELECT
                t.TheatreID,
                t.Name,
                t.BuildingName,
                t.Street,
                t.Area,
                t.City,
                t.State,
                t.PinCode
            FROM Theatre t
            WHERE t.City = ?
              AND EXISTS (
                  SELECT 1
                  FROM Screen s
                  JOIN `Show` sh
                      ON sh.ScreenID = s.ScreenID
                  WHERE s.TheatreID = t.TheatreID
                    AND sh.ShowStatus = 'SCHEDULED'
              )
            ORDER BY t.TheatreID
            """;

        return jdbcTemplate.query(
                sql,
                new TheatreListRowMapper(),
                city
        );
    }

    public List<TheatreShowDTO> findShowsByTheatreAndDate(
            Integer theatreId,
            LocalDate date
    ) {

        String sql = """
            SELECT
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
            JOIN Movie m
                ON sh.MovieID = m.MovieID
            WHERE sc.TheatreID = ?
              AND sh.ShowDate = ?
              AND sh.ShowStatus = 'SCHEDULED'
            ORDER BY sh.ShowTime
            """;

        return jdbcTemplate.query(
                sql,
                new TheatreShowRowMapper(),
                theatreId,
                date
        );
    }
}