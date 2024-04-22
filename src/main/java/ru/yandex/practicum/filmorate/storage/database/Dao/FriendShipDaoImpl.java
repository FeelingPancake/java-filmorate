package ru.yandex.practicum.filmorate.storage.database.Dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.interfacesDao.FriendShipDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class FriendShipDaoImpl implements FriendShipDao {
    private final JdbcTemplate jdbcTemplate;

    public FriendShipDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Friendship> get(Long id) {
        String sql = "SELECT * FROM user_friendships WHERE user_id = ? OR (friend_id = ? AND is_confirmed = TRUE)";

        return jdbcTemplate.query(sql, this::mapRowToFriendShip, id, id);
    }

    @Override
    public boolean add(Long userId, Long friendId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user_friendships")
                .usingGeneratedKeyColumns("is_confirmed");

        return simpleJdbcInsert.execute(Map.of("user_id", userId, "friend_id", friendId)) > 0;
    }

    @Override
    public boolean delete(Long userId, Long friendId) {
        String sql = "DELETE FROM user_friendships WHERE user_id = ? and friend_id = ?";

        return jdbcTemplate.update(sql, userId, friendId) > 0;
    }

    @Override
    public boolean update(Friendship friendShip) {
        String sql = "UPDATE user_friendships SET " +
                "is_confirmed = ?" +
                "WHERE user_id = ? and friend_id = ?";

        return jdbcTemplate.update(sql, friendShip.isConfirmed(),
                friendShip.userId(), friendShip.friendId()) > 0;
    }

    @Override
    public List<Long> getCommonFriends(Long userId1, Long userId2) {
        String sql = "SELECT uf1.friend_id FROM user_friendships uf1 " +
                "JOIN user_friendships uf2 ON uf1.friend_id = uf2.friend_id " +
                "WHERE uf1.user_id = ? " +
                "AND uf2.user_id = ? ";

        return jdbcTemplate.query(sql, (rs, rn) -> rs.getLong("friend_id"), userId1, userId2);
    }

    private Friendship mapRowToFriendShip(ResultSet resultSet, int rowNum) throws SQLException {
        return new Friendship(resultSet.getLong("user_id"), resultSet.getLong("friend_id"),
                resultSet.getBoolean("is_confirmed"));
    }
}
