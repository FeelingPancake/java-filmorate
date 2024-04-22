package ru.yandex.practicum.filmorate.storage.database.Dao;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfacesDao.FilmDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
@Primary
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;


    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public Film get(Long id) {
        String sqlQuery = "SELECT * FROM films " +
                "JOIN film_age_ratings age ON films.age_rating = age.rating_id " +
                "WHERE films.id = ?";

        List<Film> film = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id);
        if (film.isEmpty()) {
            throw new NotFoundException(id.toString());
        }
        return film.get(0);
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT * FROM films f " +
                "JOIN film_age_ratings age ON f.age_rating = age.rating_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public List<Film> getPopular(int limit) {
        String sqlQuery = "SELECT * FROM films f " +
                "JOIN film_age_ratings age ON f.age_rating = age.rating_id " +
                "ORDER BY rating desc LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, limit);
    }

    @Override
    public Long add(Film obj) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");

        Long id = simpleJdbcInsert.executeAndReturnKey(Map.of("name", obj.getName(),
                "description", obj.getDescription(),
                "release_date", obj.getReleaseDate(),
                "duration", obj.getDuration(),
                "rating", 0,
                "age_rating", obj.getMpa().id())).longValue();
        return id;
    }

    @Override
    public Long update(Film obj) {
        String sqlQuery = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ? ,duration = ?, age_rating = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sqlQuery,
                obj.getName(),
                obj.getDescription(),
                obj.getReleaseDate(),
                obj.getDuration(),
                obj.getMpa().id(),
                obj.getId());

        return obj.getId();
    }

    @Override
    public boolean delete(Long id) {
        String sqlQuery = "DELETE FROM films WHERE id = ?";

        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Long filmId = resultSet.getLong("id");
        Mpa mpa = new Mpa(resultSet.getLong("rating_id"),
                resultSet.getString("rating_name"));

        return Film.builder()
                .id(filmId)
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getObject("release_date", LocalDate.class))
                .duration(resultSet.getInt("duration"))
                .rating(resultSet.getInt("rating"))
                .mpa(mpa)
                .build();
    }
}
