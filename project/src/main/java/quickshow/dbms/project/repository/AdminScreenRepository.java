package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import quickshow.dbms.project.dto.AdminScreenDTO;
import quickshow.dbms.project.model.ScreenType;

import java.util.List;

@Repository
public class AdminScreenRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminScreenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // GET ALL SCREENS OF A THEATRE
    // =========================================================

    public List<AdminScreenDTO> findByTheatreId(
            Integer theatreId
    ) {

        String sql = """
                SELECT
                    ScreenID,
                    ScreenName,
                    ScreenType,
                    SeatingCapacity,
                    TheatreID
                FROM Screen
                WHERE TheatreID = ?
                ORDER BY ScreenID
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    AdminScreenDTO screen =
                            new AdminScreenDTO();

                    screen.setScreenId(
                            rs.getInt("ScreenID")
                    );

                    screen.setName(
                            rs.getString("ScreenName")
                    );

                    String type =
                            rs.getString("ScreenType");

                    if (type != null) {
                        screen.setScreenType(
                                ScreenType.valueOf(type)
                        );
                    }

                    screen.setCapacity(
                            rs.getInt("SeatingCapacity")
                    );

                    screen.setTheatreId(
                            rs.getInt("TheatreID")
                    );

                    return screen;
                },
                theatreId
        );
    }


    // =========================================================
    // GET SCREEN BY ID
    // =========================================================

    public AdminScreenDTO findById(
            Integer screenId
    ) {

        String sql = """
                SELECT
                    ScreenID,
                    ScreenName,
                    ScreenType,
                    SeatingCapacity,
                    TheatreID
                FROM Screen
                WHERE ScreenID = ?
                """;

        List<AdminScreenDTO> screens =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> {

                            AdminScreenDTO screen =
                                    new AdminScreenDTO();

                            screen.setScreenId(
                                    rs.getInt("ScreenID")
                            );

                            screen.setName(
                                    rs.getString("ScreenName")
                            );

                            String type =
                                    rs.getString("ScreenType");

                            if (type != null) {
                                screen.setScreenType(
                                        ScreenType.valueOf(type)
                                );
                            }

                            screen.setCapacity(
                                    rs.getInt("SeatingCapacity")
                            );

                            screen.setTheatreId(
                                    rs.getInt("TheatreID")
                            );

                            return screen;
                        },
                        screenId
                );

        if (screens.isEmpty()) {
            return null;
        }

        return screens.get(0);
    }


    // =========================================================
    // CHECK SCREEN EXISTS
    // =========================================================

    public boolean existsById(
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
    // CREATE SCREEN
    // =========================================================

    public Integer create(
            Integer theatreId,
            AdminScreenDTO screen
    ) {

        String sql = """
                INSERT INTO Screen
                (
                    ScreenName,
                    ScreenType,
                    SeatingCapacity,
                    TheatreID
                )
                VALUES (?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                screen.getName(),
                screen.getScreenType().name(),
                screen.getCapacity(),
                theatreId
        );

        return jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Integer.class
        );
    }


    // =========================================================
    // UPDATE SCREEN
    // =========================================================

    public int update(
            Integer screenId,
            AdminScreenDTO screen
    ) {

        String sql = """
                UPDATE Screen
                SET
                    ScreenName = ?,
                    ScreenType = ?,
                    SeatingCapacity = ?
                WHERE ScreenID = ?
                """;

        return jdbcTemplate.update(
                sql,
                screen.getName(),
                screen.getScreenType().name(),
                screen.getCapacity(),
                screenId
        );
    }


    // =========================================================
    // CHECK SEATS
    // =========================================================

    public boolean hasSeats(
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
    // CHECK SHOWS
    // =========================================================

    public boolean hasShows(
            Integer screenId
    ) {

        String sql = """
                SELECT COUNT(*)
                FROM `Show`
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
    // DELETE SCREEN
    // =========================================================

    public int delete(
            Integer screenId
    ) {

        String sql = """
                DELETE FROM Screen
                WHERE ScreenID = ?
                """;

        return jdbcTemplate.update(
                sql,
                screenId
        );
    }
}