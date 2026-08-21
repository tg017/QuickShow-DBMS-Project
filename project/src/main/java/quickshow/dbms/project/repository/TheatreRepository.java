package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.Theatre;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class TheatreRepository {

    private final JdbcTemplate jdbcTemplate;

    public TheatreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CREATE
    // =========================================================

    public Theatre create(Theatre theatre) {

        String sql = """
                INSERT INTO Theatre
                    (
                        Name,
                        ContactNo,
                        BuildingName,
                        Street,
                        Area,
                        City,
                        State,
                        PinCode
                    )
                VALUES
                    (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder =
                new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            statement.setString(
                    1,
                    theatre.getName()
            );

            statement.setString(
                    2,
                    theatre.getContactNo()
            );

            statement.setString(
                    3,
                    theatre.getBuildingName()
            );

            statement.setString(
                    4,
                    theatre.getStreet()
            );

            statement.setString(
                    5,
                    theatre.getArea()
            );

            statement.setString(
                    6,
                    theatre.getCity()
            );

            statement.setString(
                    7,
                    theatre.getState()
            );

            statement.setString(
                    8,
                    theatre.getPinCode()
            );

            return statement;

        }, keyHolder);

        Number generatedId =
                keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException(
                    "TheatreID was not generated"
            );
        }

        theatre.setTheatreId(
                generatedId.intValue()
        );

        return theatre;
    }


    // =========================================================
    // READ BY ID
    // =========================================================

    public Theatre findById(Integer theatreId) {

        String sql = """
                SELECT
                    TheatreID,
                    Name,
                    ContactNo,
                    BuildingName,
                    Street,
                    Area,
                    City,
                    State,
                    PinCode
                FROM Theatre
                WHERE TheatreID = ?
                """;

        List<Theatre> theatres =
                jdbcTemplate.query(
                        sql,
                        new TheatreRowMapper(),
                        theatreId
                );

        if (theatres.isEmpty()) {
            return null;
        }

        return theatres.get(0);
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<Theatre> findAll() {

        String sql = """
                SELECT
                    TheatreID,
                    Name,
                    ContactNo,
                    BuildingName,
                    Street,
                    Area,
                    City,
                    State,
                    PinCode
                FROM Theatre
                ORDER BY TheatreID
                """;

        return jdbcTemplate.query(
                sql,
                new TheatreRowMapper()
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    public int update(Theatre theatre) {

        String sql = """
                UPDATE Theatre
                SET
                    Name = ?,
                    ContactNo = ?,
                    BuildingName = ?,
                    Street = ?,
                    Area = ?,
                    City = ?,
                    State = ?,
                    PinCode = ?
                WHERE TheatreID = ?
                """;

        return jdbcTemplate.update(
                sql,
                theatre.getName(),
                theatre.getContactNo(),
                theatre.getBuildingName(),
                theatre.getStreet(),
                theatre.getArea(),
                theatre.getCity(),
                theatre.getState(),
                theatre.getPinCode(),
                theatre.getTheatreId()
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    public int deleteById(Integer theatreId) {

        String sql = """
                DELETE FROM Theatre
                WHERE TheatreID = ?
                """;

        return jdbcTemplate.update(
                sql,
                theatreId
        );
    }


    // =========================================================
    // EXISTS
    // =========================================================

    public boolean existsById(Integer theatreId) {

        String sql = """
                SELECT COUNT(*)
                FROM Theatre
                WHERE TheatreID = ?
                """;

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        theatreId
                );

        return count != null && count > 0;
    }


    // =========================================================
    // FIND BY CITY
    // =========================================================

    public List<Theatre> findByCity(String city) {

        String sql = """
                SELECT
                    TheatreID,
                    Name,
                    ContactNo,
                    BuildingName,
                    Street,
                    Area,
                    City,
                    State,
                    PinCode
                FROM Theatre
                WHERE City = ?
                ORDER BY Name
                """;

        return jdbcTemplate.query(
                sql,
                new TheatreRowMapper(),
                city
        );
    }


    // =========================================================
    // FIND BY CITY AND AREA
    // =========================================================

    public List<Theatre> findByCityAndArea(
            String city,
            String area
    ) {

        String sql = """
                SELECT
                    TheatreID,
                    Name,
                    ContactNo,
                    BuildingName,
                    Street,
                    Area,
                    City,
                    State,
                    PinCode
                FROM Theatre
                WHERE City = ?
                  AND Area = ?
                ORDER BY Name
                """;

        return jdbcTemplate.query(
                sql,
                new TheatreRowMapper(),
                city,
                area
        );
    }
}