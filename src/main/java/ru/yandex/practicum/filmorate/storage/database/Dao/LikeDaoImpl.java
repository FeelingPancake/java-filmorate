package ru.yandex.practicum.filmorate.storage.database.Dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.interfacesDao.LikeDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class LikeDaoImpl implements LikeDao {
    private final JdbcTemplate jdbcTemplate;

    public LikeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Like> get(Long generalId) {
        String sql = "SELECT * FROM liked_by WHERE film_id = ?";

        return jdbcTemplate.query(sql, this::mapRowToLike, generalId);
    }

    @Override
    public boolean add(Like... likes) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("liked_by");

        List<Map<String, Long>> arr = new LinkedList<>();
        for (Like like : likes) {
            arr.add(Map.of("user_id", like.userId(), "film_id", like.filmId()));
        }

        return (simpleJdbcInsert.executeBatch(SqlParameterSourceUtils.createBatch(arr)).length) > 0;
    }

    @Override
    public boolean delete(Like like) {
        String sqlQuery = "DELETE FROM liked_by WHERE user_id = ? AND film_id = ?";

        return jdbcTemplate.update(sqlQuery, like.userId(), like.userId()) > 0;
    }

    @Override
    public boolean deleteAllLikesToFilm(Long filmId) {
        String sqlQuery = "DELETE FROM liked_by WHERE film_id = ?";

        return jdbcTemplate.update(sqlQuery, filmId) > 0;
    }


    private Like mapRowToLike(ResultSet resultSet, int rowNum) throws SQLException {
        return new Like(resultSet.getLong("user_id"), resultSet.getLong("film_id"));
    }
}
