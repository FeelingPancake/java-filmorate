package ru.yandex.practicum.filmorate.storage.database.Dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.SqlExecuteException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfacesDao.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> get(Long film) {
        String sql = "SELECT * FROM genres WHERE film_id = ?";

        return jdbcTemplate.query(sql, this::mapRowToGenre, film);
    }

    @Override
    public boolean add(Long filmId, Long genreId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("genres");
        try {
            getGenreById(genreId);
        } catch (IdNotFoundException e) {

            throw new SqlExecuteException(simpleJdbcInsert.toString());
        }

        if (getGenresIdForFilm(filmId).contains(genreId)) {
            return false;
        }

        return simpleJdbcInsert.execute(Map.of("film_id", filmId, "genre_id", genreId)) > 0;
    }

    @Override
    public boolean add(Long filmId, Long... genreId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("genres");

        List<Map<String, Long>> arr = new LinkedList<>();
        for (Long id : genreId) {

            try {
                getGenreById(id);
            } catch (IdNotFoundException e) {
                throw new SqlExecuteException(simpleJdbcInsert.toString());
            }
                arr.add(Map.of("film_id", filmId, "genre_id", id));
        }
        arr = arr.stream().distinct().toList();

        return (simpleJdbcInsert.executeBatch(SqlParameterSourceUtils.createBatch(arr)).length) > 0;
    }

    @Override
    public boolean delete(Long filmid, Genre genre) {
        String sql = "DELETE FROM genres WHERE film_id = ? and genre_id = ?";

        return jdbcTemplate.update(sql, filmid, genre.id()) > 0;
    }

    @Override
    public boolean deleteAllGenresOfFilm(Long filmId) throws IdNotFoundException {
        String sql = "DELETE FROM genres WHERE film_id = ?";

        return jdbcTemplate.update(sql, filmId) > 0;
    }

    @Override
    public Long addNewGenre(String name) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("genre").usingGeneratedKeyColumns("genre_id");
        return simpleJdbcInsert.executeAndReturnKey(Map.of("genre_name", name)).longValue();
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genre ORDER BY genre_id";

        return jdbcTemplate.query(sql, ((rs, rowNum) ->
                new Genre(rs.getLong("genre_id"),rs.getString("genre_name"))));
    }

    @Override
    public Genre getGenreById(long genreId) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql,
                    (rs,rn )-> new Genre(rs.getLong("genre_id"), rs.getString("genre_name")), genreId);

        } catch (EmptyResultDataAccessException e) {
            throw new IdNotFoundException(genreId);
        }
    }

    @Override
    public List<Long> getGenresIdForFilm(Long filmId) {
        String sql = "SELECT * FROM genres WHERE film_id = ?";

        return jdbcTemplate.query(sql, ((rs, rowNum) -> rs.getLong("genre_id")), filmId);
    }

    private Long getGenreIdByName(String name) throws IdNotFoundException {
        String sql = "SELECT genre_id FROM genre WHERE genre_name = ?";

        List<Long> genreId = jdbcTemplate.query(sql, (rs, r) -> rs.getLong("genre_id"), name);

        if (genreId.isEmpty()) {
            throw new IdNotFoundException(-1L);
        }

        return genreId.get(0);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return getGenreById(resultSet.getLong("genre_id"));
    }
}
