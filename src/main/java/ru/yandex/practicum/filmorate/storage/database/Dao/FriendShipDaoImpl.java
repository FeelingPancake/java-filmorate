package ru.yandex.practicum.filmorate.storage.database.Dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FriendShip;
import ru.yandex.practicum.filmorate.storage.interfacesDao.FriendShipDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public class FriendShipDaoImpl implements FriendShipDao {
    private final JdbcTemplate jdbcTemplate;

    public FriendShipDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<FriendShip> get(Long id) {
        String sql = "SELECT * FROM friendship WHERE user_id = ? OR (friend_id = ? AND is_confirmed = TRUE)";

        return jdbcTemplate.query(sql, this::mapRowToFriendShip, id, id);
    }

    @Override
    public boolean add(FriendShip... friendShip) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friendship")
                .usingGeneratedKeyColumns("is_confirmed");

        List<Map<String, Long>> arr = new LinkedList<>();
        for (FriendShip friend : friendShip) {
            arr.add(Map.of("user_id", friend.userId(), "friend_id", friend.friendId()));
        }

        try {
            return simpleJdbcInsert.executeBatch(SqlParameterSourceUtils.createBatch(arr)).length > 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Override
    public boolean delete(FriendShip friendShip) {
        String sql = "DELETE FROM friendship WHERE user_id = ? and friend_id = ?";

        return jdbcTemplate.update(sql, friendShip.userId(), friendShip.friendId()) > 0;
    }

    @Override
    public boolean deleteAllFriends(Long userId) {
        String sql = "DELETE FROM friendship WHERE user_id = ?";

        return jdbcTemplate.update(sql, userId) > 0;
    }

    public boolean update(FriendShip friendShip) {
        String sql = "UPDATE friendship SET " +
                "is_confirmed = ?" +
                "WHERE user_id = ? and friend_id = ?";

        return jdbcTemplate.update(sql, friendShip.isConfirmed(),
                friendShip.userId(), friendShip.friendId()) > 0;
    }

    private FriendShip mapRowToFriendShip(ResultSet resultSet, int rowNum) throws SQLException {
        return new FriendShip(resultSet.getLong("user_id"), resultSet.getLong("friend_id"),
                resultSet.getBoolean("is_confirmed"));
    }
}
