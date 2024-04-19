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
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.FriendShip;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.Dao.FriendShipDaoImpl;
import ru.yandex.practicum.filmorate.storage.database.DaoStorage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.interfacesDao.FriendShipDao;

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
        Long id = userStorage.add(newUser);

        User savedUser = userStorage.get(id);
        User user = userStorage.get(id);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    public void testFindAllUsers() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate, friendShip);
        List<Long> list = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            User newUser = User.builder()
                    .id((long) i)
                    .email("user@email.ru")
                    .login("vanya123" + i)
                    .name("Ivan Petrov")
                    .birthday(LocalDate.of(1990, 1, i)).build();
            Long id = userStorage.add(newUser);
            list.add(id);
        }

        List<Long> users = userStorage.getAll().stream().map(x -> x.getId()).toList();

        Assertions.assertIterableEquals(list, users);

        for (int i = 0; i < users.size(); i++) {
            assertThat(userStorage.get(users.get(i))).isNotNull().usingRecursiveComparison().isEqualTo(userStorage.get(list.get(i)));
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
        Long id = userStorage.add(newUser);
        User oldUser = userStorage.get(id);

        Long newId = userStorage.update(updatedUser);
        User savedUser = userStorage.get(newId);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isNotEqualTo(oldUser);

        assertEquals(oldUser.getId(), savedUser.getId());
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
        Long id = userStorage.add(newUser);
        userStorage.delete(id);

        assertThrows(IdNotFoundException.class, () -> {
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
        Long userId = userStorage.add(newUser);
        Long friendId = userStorage.add(friend);

        userStorage.addFriend(userId, friendId);

        List<FriendShip> friendShips = userStorage.get(userId).getFriendsList();
        List<FriendShip> expected = new ArrayList<>();
        expected.add(new FriendShip(userId, friendId, false));

        assertThat(userStorage.get(friendId).getFriendsList()).isEmpty();
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
        Long userId = userStorage.add(newUser);
        Long friendId = userStorage.add(friend);

        userStorage.addFriend(userId, friendId);
        userStorage.deleteFriend(new FriendShip(userId, friendId, false));

        assertThat(userStorage.get(userId).getFriendsList()).isEmpty();
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
        Long userId = userStorage.add(newUser);
        Long friendId1 = userStorage.add(friend1);
        Long friendId2 = userStorage.add(friend2);

        userStorage.addFriend(userId, friendId1);

        assertFalse(userStorage.addFriend(userId, friendId1));
    }


}

