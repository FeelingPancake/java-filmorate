package ru.yandex.practicum.filmorate.storage.database.DaoStorage;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.FriendShip;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfacesDao.FriendShipDao;
import ru.yandex.practicum.filmorate.storage.interfacesDao.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Repository
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FriendShipDao friendShipDao;

    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendShipDao friendShipDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendShipDao = friendShipDao;
    }

    @Override
    public User get(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (DataAccessException e) {
            throw new IdNotFoundException(id);
        }
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT * FROM users";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public Long add(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");


        return simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
    }

    @Override
    public Long update(User obj) {
        String sqlQuery = "UPDATE users SET " +
                "email = ?, login = ?, name = ? , birthday = ? " +
                "where id = ?";
        try {
            jdbcTemplate.update(sqlQuery,
                    obj.getEmail(),
                    obj.getLogin(),
                    obj.getName(),
                    obj.getBirthday(),
                    obj.getId());
        } catch (DataAccessException e) {
            throw new IdNotFoundException(obj.getId());
        }
        return obj.getId();
    }

    @Override
    public boolean delete(Long id) {
        String sqlQuery = "DELETE FROM users WHERE id = ?";
        friendShipDao.deleteAllFriends(id);

        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        if (get(friendId) == null) {
            throw new IdNotFoundException(friendId);
        }
        return friendShipDao.add(new FriendShip(userId, friendId, false));
    }

    @Override
    public boolean deleteFriend(FriendShip friendShip) {
        return friendShipDao.delete(friendShip);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {

        List<FriendShip> friends = friendShipDao.get(resultSet.getLong("id"))
                .isEmpty() ? Collections.emptyList() : friendShipDao.get(resultSet.getLong("id"));
        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getObject("birthday", LocalDate.class))
                .friendsList(friends)
                .build();
    }
}
