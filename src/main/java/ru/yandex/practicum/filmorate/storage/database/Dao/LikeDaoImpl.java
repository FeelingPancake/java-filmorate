package ru.yandex.practicum.filmorate.storage.database.Dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.interfacesDao.LikeDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class LikeDaoImpl implements LikeDao {
    private final JdbcTemplate jdbcTemplate;

    public LikeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Like> get(Long filmId) {
        String sql = "SELECT * FROM film_liked_by WHERE film_id = ?";

        return jdbcTemplate.query(sql, this::mapRowToLike, filmId);
    }

    @Override
    public boolean add(Like like) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("film_liked_by");

        boolean isAdded = simpleJdbcInsert.execute(Map.of(
                "user_id", like.userId(), "film_id", like.filmId())) > 0;
        if (isAdded) {
            return updateRating(like.filmId());
        } else {
            return false;
        }
    }

    @Override
    public boolean delete(Like like) {
        String sqlQuery = "DELETE FROM film_liked_by WHERE user_id = ? AND film_id = ?";

        boolean isDeleted = jdbcTemplate.update(sqlQuery, like.userId(), like.userId()) > 0;

        if (isDeleted) {
            return updateRating(like.filmId());
        } else {
            return false;
        }
    }

    private boolean updateRating(Long filmId) {
        String sql = "SELECT count(*) as count FROM film_liked_by where film_id = ?";
        Integer rating = jdbcTemplate.query(sql, (rs, rn) ->
                rs.getInt("count"), filmId).get(0);

        String sqlUpdate = "UPDATE films SET rating = ? WHERE id = ?";
        return jdbcTemplate.update(sqlUpdate, rating, filmId) > 0;
    }

    private Like mapRowToLike(ResultSet resultSet, int rowNum) throws SQLException {
        return new Like(resultSet.getLong("user_id"), resultSet.getLong("film_id"));
    }
}
