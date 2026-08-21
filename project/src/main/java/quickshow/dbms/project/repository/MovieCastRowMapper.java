package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.RowMapper;
import quickshow.dbms.project.model.MovieCast;
import quickshow.dbms.project.model.MovieCastId;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MovieCastRowMapper
        implements RowMapper<MovieCast> {

    @Override
    public MovieCast mapRow(
            ResultSet rs,
            int rowNum
    ) throws SQLException {

        MovieCast movieCast =
                new MovieCast();

        MovieCastId id =
                new MovieCastId();

        id.setMovieId(
                rs.getInt("MovieID")
        );

        id.setActor(
                rs.getString("Actor")
        );

        movieCast.setId(id);

        return movieCast;
    }
}