package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.dto.MovieSummaryDTO;
import quickshow.dbms.project.dto.ScreenSummaryDTO;
import quickshow.dbms.project.dto.SeatLayoutDTO;
import quickshow.dbms.project.dto.TheatreSummaryDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

public class SeatLayoutShowRowMapper
        implements RowMapper<SeatLayoutDTO> {

    @Override
    public SeatLayoutDTO mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        MovieSummaryDTO movie =
                new MovieSummaryDTO(
                        rs.getInt("MovieID"),
                        rs.getString("MovieTitle"),
                        rs.getString("Poster")
                );

        TheatreSummaryDTO theatre =
                new TheatreSummaryDTO(
                        rs.getInt("TheatreID"),
                        rs.getString("TheatreName"),
                        rs.getString("City")
                );

        ScreenSummaryDTO screen =
                new ScreenSummaryDTO(
                        rs.getInt("ScreenID"),
                        rs.getString("ScreenName"),
                        rs.getString("ScreenType")
                );

        return new SeatLayoutDTO(
                rs.getInt("ShowID"),
                movie,
                theatre,
                screen,
                rs.getDate("ShowDate") != null
                        ? rs.getDate("ShowDate").toLocalDate()
                        : null,
                rs.getTime("ShowTime") != null
                        ? rs.getTime("ShowTime").toLocalTime()
                        : null,
                rs.getLong("TicketPrice"),
                rs.getInt("AvailableSeats"),
                rs.getString("ShowStatus"),
                Collections.emptyList()
        );
    }
}