package quickshow.dbms.project.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import quickshow.dbms.project.model.Screen;
import quickshow.dbms.project.model.ScreenType;
import quickshow.dbms.project.model.Theatre;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ScreenRepository {

    private final JdbcTemplate jdbcTemplate;

    public ScreenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // =========================================================
    // CREATE
    // =========================================================

    public Screen create(
            Screen screen,
            Integer theatreId
    ) {

        String sql = """
                INSERT INTO Screen
                    (
                        ScreenName,
                        ScreenType,
                        SeatingCapacity,
                        TheatreID
                    )
                VALUES
                    (?, ?, ?, ?)
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
                    screen.getName()
            );

            statement.setString(
                    2,
                    screen.getScreenType().name()
            );

            statement.setInt(
                    3,
                    screen.getCapacity()
            );

            statement.setInt(
                    4,
                    theatreId
            );

            return statement;

        }, keyHolder);

        Number generatedId =
                keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException(
                    "ScreenID was not generated"
            );
        }

        screen.setScreenId(
                generatedId.intValue()
        );

        return screen;
    }


    // =========================================================
    // READ BY ID
    // =========================================================

    public Screen findById(Integer screenId) {

        String sql = """
                SELECT
                    ScreenID,
                    ScreenName,
                    ScreenType,
                    SeatingCapacity
                FROM Screen
                WHERE ScreenID = ?
                """;

        List<Screen> screens =
                jdbcTemplate.query(
                        sql,
                        new ScreenRowMapper(),
                        screenId
                );

        if (screens.isEmpty()) {
            return null;
        }

        return screens.get(0);
    }


    // =========================================================
    // READ ALL
    // =========================================================

    public List<Screen> findAll() {

        String sql = """
                SELECT
                    ScreenID,
                    ScreenName,
                    ScreenType,
                    SeatingCapacity
                FROM Screen
                ORDER BY ScreenID
                """;

        return jdbcTemplate.query(
                sql,
                new ScreenRowMapper()
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    public int update(
            Screen screen
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
                screen.getScreenId()
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    public int deleteById(Integer screenId) {

        String sql = """
                DELETE FROM Screen
                WHERE ScreenID = ?
                """;

        return jdbcTemplate.update(
                sql,
                screenId
        );
    }


    // =========================================================
    // EXISTS
    // =========================================================

    public boolean existsById(Integer screenId) {

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
    // FIND BY THEATRE
    // =========================================================

    public List<Screen> findByTheatre(
            Integer theatreId
    ) {

        String sql = """
                SELECT
                    ScreenID,
                    ScreenName,
                    ScreenType,
                    SeatingCapacity
                FROM Screen
                WHERE TheatreID = ?
                ORDER BY ScreenID
                """;

        return jdbcTemplate.query(
                sql,
                new ScreenRowMapper(),
                theatreId
        );
    }


    // =========================================================
    // FIND BY SCREEN TYPE
    // =========================================================

    public List<Screen> findByScreenType(
            ScreenType screenType
    ) {

        String sql = """
                SELECT
                    ScreenID,
                    ScreenName,
                    ScreenType,
                    SeatingCapacity
                FROM Screen
                WHERE ScreenType = ?
                ORDER BY ScreenID
                """;

        return jdbcTemplate.query(
                sql,
                new ScreenRowMapper(),
                screenType.name()
        );
    }


    // =========================================================
    // FIND BY THEATRE AND SCREEN TYPE
    // =========================================================

    public List<Screen> findByTheatreAndScreenType(
            Integer theatreId,
            ScreenType screenType
    ) {

        String sql = """
                SELECT
                    ScreenID,
                    ScreenName,
                    ScreenType,
                    SeatingCapacity
                FROM Screen
                WHERE TheatreID = ?
                  AND ScreenType = ?
                ORDER BY ScreenID
                """;

        return jdbcTemplate.query(
                sql,
                new ScreenRowMapper(),
                theatreId,
                screenType.name()
        );
    }
}