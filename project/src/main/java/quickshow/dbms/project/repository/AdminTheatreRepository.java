package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import quickshow.dbms.project.dto.TheatreListDTO;

import java.util.List;

@Repository
public class AdminTheatreRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminTheatreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // =========================================================
    // GET ALL THEATRES
    // =========================================================

    public List<TheatreListDTO> findAll() {

        String sql = """
                SELECT
                    TheatreID,
                    Name,
                    BuildingName,
                    Street,
                    Area,
                    City,
                    State,
                    PinCode
                FROM Theatre
                ORDER BY Name
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new TheatreListDTO(
                        rs.getInt("TheatreID"),
                        rs.getString("Name"),
                        rs.getString("BuildingName"),
                        rs.getString("Street"),
                        rs.getString("Area"),
                        rs.getString("City"),
                        rs.getString("State"),
                        rs.getString("PinCode")
                )
        );
    }


    // =========================================================
    // GET THEATRE BY ID
    // =========================================================

    public TheatreListDTO findById(Integer theatreId) {

        String sql = """
                SELECT
                    TheatreID,
                    Name,
                    BuildingName,
                    Street,
                    Area,
                    City,
                    State,
                    PinCode
                FROM Theatre
                WHERE TheatreID = ?
                """;

        List<TheatreListDTO> theatres = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new TheatreListDTO(
                        rs.getInt("TheatreID"),
                        rs.getString("Name"),
                        rs.getString("BuildingName"),
                        rs.getString("Street"),
                        rs.getString("Area"),
                        rs.getString("City"),
                        rs.getString("State"),
                        rs.getString("PinCode")
                ),
                theatreId
        );

        if (theatres.isEmpty()) {
            return null;
        }

        return theatres.get(0);
    }


    // =========================================================
    // CHECK THEATRE EXISTS
    // =========================================================

    public boolean existsById(Integer theatreId) {

        String sql = """
                SELECT COUNT(*)
                FROM Theatre
                WHERE TheatreID = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                theatreId
        );

        return count != null && count > 0;
    }


    // =========================================================
    // CREATE THEATRE
    // =========================================================

    public Integer create(TheatreListDTO theatre) {

        String sql = """
                INSERT INTO Theatre
                (
                    Name,
                    BuildingName,
                    Street,
                    Area,
                    City,
                    State,
                    PinCode
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                theatre.getName(),
                theatre.getBuildingName(),
                theatre.getStreet(),
                theatre.getArea(),
                theatre.getCity(),
                theatre.getState(),
                theatre.getPinCode()
        );

        return jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Integer.class
        );
    }


    // =========================================================
    // UPDATE THEATRE
    // =========================================================

    public int update(
            Integer theatreId,
            TheatreListDTO theatre
    ) {

        String sql = """
                UPDATE Theatre
                SET
                    Name = ?,
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
                theatre.getBuildingName(),
                theatre.getStreet(),
                theatre.getArea(),
                theatre.getCity(),
                theatre.getState(),
                theatre.getPinCode(),
                theatreId
        );
    }


    // =========================================================
    // CHECK FOR SCREENS
    // =========================================================

    public boolean hasScreens(Integer theatreId) {

        String sql = """
                SELECT COUNT(*)
                FROM Screen
                WHERE TheatreID = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                theatreId
        );

        return count != null && count > 0;
    }


    // =========================================================
    // DELETE THEATRE
    // =========================================================

    public int delete(Integer theatreId) {

        String sql = """
                DELETE FROM Theatre
                WHERE TheatreID = ?
                """;

        return jdbcTemplate.update(
                sql,
                theatreId
        );
    }
}