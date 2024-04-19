package ru.yandex.practicum.filmorate.integrityTests;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.SqlExecuteException;
import ru.yandex.practicum.filmorate.model.FriendShip;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfacesDao.FriendShipDao;
import ru.yandex.practicum.filmorate.storage.database.Dao.FriendShipDaoImpl;
import ru.yandex.practicum.filmorate.storage.database.DaoStorage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(FriendShipDaoImpl.class)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final FriendShipDao friendShip;

    @AfterEach
    void reset() {
        String sql = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1;";

        jdbcTemplate.execute(sql);
    }

    @Test
    public void testFindUserById() {
        User newUser = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1)).build();
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate, friendShip);
        userStorage.add(newUser);

        User savedUser = userStorage.get(1L);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testFindAllUsers() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate, friendShip);
        List<User> list = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            User newUser = User.builder()
                    .id((long) i)
                    .email("user@email.ru")
                    .login("vanya123" + i)
                    .name("Ivan Petrov")
                    .birthday(LocalDate.of(1990, 1, i)).build();
            userStorage.add(newUser);
            list.add(newUser);
        }

        List<User> users = userStorage.getAll();

        Assertions.assertIterableEquals(list, users);

        for (int i = 0; i < users.size(); i++) {
            assertThat(users.get(i)).isNotNull().usingRecursiveComparison().isEqualTo(list.get(i));
        }
    }

    @Test
    public void testUpdateUser() {
        User newUser = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1)).build();

        User updatedUser = newUser.toBuilder().name("Petr Ivanov").build();
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate, friendShip);
        userStorage.add(newUser);
        userStorage.update(updatedUser);

        User savedUser = userStorage.get(1L);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedUser);
    }

    @Test
    public void testDeleteUser() {
        User newUser = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1)).build();

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate, friendShip);
        userStorage.add(newUser);
        userStorage.delete(1L);

        assertThrows(SqlExecuteException.class, () -> {
            userStorage.get(1L);
        });
    }

    @Test
    public void testAddingFriendToUserWithDefaultValueFalse() {
        User newUser = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1)).build();

        User friend = User.builder()
                .email("user1@email.ru")
                .login("sanek123")
                .name("Sasha Petrov")
                .birthday(LocalDate.of(1991, 2, 1)).build();

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate, friendShip);
        userStorage.add(newUser);
        userStorage.add(friend);

        userStorage.addFriend(1L, 2L);

        List<FriendShip> friendShips = userStorage.get(1L).getFriendsList();
        List<FriendShip> expected = new ArrayList<>();
        expected.add(new FriendShip(1L, 2L, false));

        assertThat(userStorage.get(2L).getFriendsList()).isNull();
        assertIterableEquals(expected, friendShips);
    }

    @Test
    public void testDeleteFriend() {
        User newUser = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1)).build();

        User friend = User.builder()
                .email("user1@email.ru")
                .login("sanek123")
                .name("Sasha Petrov")
                .birthday(LocalDate.of(1991, 2, 1)).build();

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate, friendShip);
        userStorage.add(newUser);
        userStorage.add(friend);

        userStorage.addFriend(1L, 2L);
        userStorage.deleteFriend(new FriendShip(1L, 2L, false));

        assertThat(userStorage.get(1L).getFriendsList()).isNull();
    }

    @Test
    public void testAddFriendTwoTimes() {
        User newUser = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1)).build();

        User friend1 = User.builder()
                .email("user1@email.ru")
                .login("sanek123")
                .name("Sasha Petrov")
                .birthday(LocalDate.of(1991, 2, 1)).build();

        User friend2 = User.builder()
                .email("user2@email.ru")
                .login("Hastur123")
                .name("Faruh al Hazred")
                .birthday(LocalDate.of(1991, 4, 12)).build();

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate, friendShip);
        userStorage.add(newUser);
        userStorage.add(friend1);
        userStorage.add(friend2);

        userStorage.addFriend(1L, 2L);

        assertFalse(userStorage.addFriend(1L, 2L));
    }


}

