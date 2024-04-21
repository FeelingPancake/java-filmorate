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

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (DataAccessException e) {
            throw new NotFoundException(id.toString());
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
