package ru.yandex.practicum.filmorate.storage.database.Dao;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.SqlExecuteException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfacesDao.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Repository
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> get(Long filmId) {
        String sql = "SELECT * FROM film_genres " +
                "JOIN genres ON film_genres.genre_id = genres.genre_id " +
                "WHERE film_genres.film_id = ? " +
                "GROUP BY film_genres.genre_id, film_genres.film_id, genres.genre_name;";

            List<Genre> genres = jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
            return genres;
    }

    @Override
    public boolean add(Long filmId, List<Genre> genre) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("film_genres");

        List<Map<String, Long>> list = new HashSet<>(genre).stream().map(x ->
                Map.of("film_id", filmId, "genre_id", x.id())).toList();

        try {
          return simpleJdbcInsert.executeBatch(SqlParameterSourceUtils.createBatch(list)).length > 0;
        } catch (DataIntegrityViolationException e) {
            throw new SqlExecuteException(simpleJdbcInsert.toString());
        }
    }

    @Override
    public boolean delete(Long filmId, Long genreId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ? and genre_id = ?";

        return jdbcTemplate.update(sql, filmId, genreId) > 0;
    }

    @Override
    public Long addNewGenre(String name) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("genres").usingGeneratedKeyColumns("genre_id");
        return simpleJdbcInsert.executeAndReturnKey(Map.of("genre_name", name)).longValue();
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres ORDER BY genre_id";

        return jdbcTemplate.query(sql, ((rs, rowNum) ->
                new Genre(rs.getLong("genre_id"), rs.getString("genre_name"))));
    }

    @Override
    public List<Genre> getGenreForId(Long genreId) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";

        return jdbcTemplate.query(sql, ((rs, rowNum) ->
                new Genre(rs.getLong("genre_id"), rs.getString("genre_name"))), genreId);
    }

    @Override
    public List<Long> getGenresIdForFilm(Long filmId) {
        String sql = "SELECT * FROM film_genres WHERE film_id = ?";

        return jdbcTemplate.query(sql, ((rs, rowNum) -> rs.getLong("genre_id")), filmId);
    }

    private Long getGenreIdByName(String name) {
        String sql = "SELECT genre_id FROM genres WHERE genre_name = ?";

        List<Long> genreId = jdbcTemplate.query(sql, (rs, r) -> rs.getLong("genre_id"), name);

        if (genreId.isEmpty()) {
            throw new NotFoundException("-1L");
        }

        return genreId.get(0);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getLong("genre_id"),resultSet.getString("genre_name"));
    }
}
