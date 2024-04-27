package ru.yandex.practicum.filmorate.storage.database.Dao;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfacesDao.UserDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
@Primary
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User get(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public List<User> getFriends(Long id) {
        String sql = "SELECT * FROM users AS u " +
                "JOIN user_friendships f ON (u.id = f.user_id AND f.friend_id = ? AND f.is_confirmed = TRUE) " +
                "OR (u.id = f.friend_id AND f.user_id = ?)";
        return jdbcTemplate.query(sql, this::mapRowToUser, id, id);
    }

    @Override
    public List<User> getCommonFriendsUsers(Long userId1, Long userId2) {
        String sql = "SELECT u.* " +
                "FROM users u " +
                "JOIN user_friendships uf1 ON u.id = uf1.friend_id " +
                "JOIN user_friendships uf2 ON uf1.friend_id = uf2.friend_id " +
                "WHERE uf1.user_id = ? AND uf2.user_id = ?";

        return jdbcTemplate.query(sql, this::mapRowToUser, userId1, userId2);
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
            throw new NotFoundException(obj.getId().toString());
        }
        return obj.getId();
    }

    @Override
    public boolean delete(Long id) {
        String sqlQuery = "DELETE FROM users WHERE id = ?";

        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {

        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getObject("birthday", LocalDate.class))
                .build();
    }
}
