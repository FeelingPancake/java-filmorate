package ru.yandex.practicum.filmorate.storage.database.DaoStorage;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.SqlExecuteException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfacesDao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfacesDao.GenreDao;
import ru.yandex.practicum.filmorate.storage.interfacesDao.LikeDao;
import ru.yandex.practicum.filmorate.storage.interfacesDao.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final LikeDao likeDao;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDao genreDao,
                         MpaDao mpaDao, LikeDao likeDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.likeDao = likeDao;
    }

    @Override
    public Film get(Long id) {
        String sqlQuery = "SELECT * FROM film WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (DataAccessException e) {
            throw new IdNotFoundException(id);
        }
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT * FROM film";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public List<Film> getAll(int limit) {
        String sqlQuery = "SELECT * FROM film ORDER BY rating desc LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, limit);
    }

    @Override
    public Long add(Film obj) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("id");


        try {
            mpaDao.get(obj.getMpa().id());
        } catch (IdNotFoundException e) {
            throw new SqlExecuteException(simpleJdbcInsert.toString());
        }

        Long id = simpleJdbcInsert.executeAndReturnKey(Map.of("name", obj.getName(),
                "description", obj.getDescription(),
                "release_date", obj.getReleaseDate(),
                "duration", obj.getDuration(),
                "rating", 0,
                "age_rating", obj.getMpa().id())).longValue();

        if (obj.getGenres() != null) {
            Long[] genres = obj.getGenres().stream().map(Genre::id).toArray(Long[]::new);
            genreDao.add(id, genres);
        }
        return id;
    }

    @Override
    public Long update(Film obj) {
        String sqlQuery = "UPDATE film SET " +
                "name = ?, description = ?, release_date = ? ,duration = ?, age_rating = ? " +
                "WHERE id = ?";

        genreDao.deleteAllGenresOfFilm(obj.getId());
        if (obj.getGenres() != null) {
            Long[] genres = obj.getGenres().stream().map(Genre::id).toArray(Long[]::new);
            genreDao.add(obj.getId(), genres);
        }

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
        String sqlQuery = "DELETE FROM film WHERE id = ?";
        try {
            genreDao.deleteAllGenresOfFilm(id);
            likeDao.deleteAllLikesToFilm(id);
        } catch (IdNotFoundException e) {
            throw new IdNotFoundException(id);
        }

        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public void likeFilm(Long userId, Long filmId) {
        if (likeDao.add(new Like(userId, filmId))) {
            String sql = "UPDATE film SET rating = rating + 1 WHERE id = ?";
            jdbcTemplate.update(sql, filmId);
        }
    }

    @Override
    public void dislikeFilm(Long userId, Long filmId) {
        if (likeDao.delete(new Like(userId, filmId))) {
            String sql = "UPDATE film SET rating = rating - 1 WHERE id = ?";
            jdbcTemplate.update(sql, filmId);
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Long filmId = resultSet.getLong("id");
        List<Genre> genres = genreDao.get(filmId);
        Mpa ageRating = mpaDao.get(resultSet.getLong("age_rating"));
        List<Like> likes = likeDao.get(filmId).isEmpty() ? Collections.emptyList() : likeDao.get(filmId);


        return Film.builder()
                .id(filmId)
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getObject("release_date", LocalDate.class))
                .duration(resultSet.getInt("duration"))
                .rating(resultSet.getInt("rating"))
                .genres(genres)
                .mpa(ageRating)
                .likedBy(likes)
                .build();
    }
}
