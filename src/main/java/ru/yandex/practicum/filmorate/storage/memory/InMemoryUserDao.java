package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UpdateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfacesDao.UserDao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class InMemoryUserDao implements UserDao {
    private final Map<Long, User> users;
    private long id = 1;

    public InMemoryUserDao() {
        this.users = new HashMap<>();
    }

    @Override
    public User get(Long id) {
        if (!(users.get(id) == null)) {
            throw new NotFoundException(id.toString());
        }
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Long add(User obj) {
        User user = obj.toBuilder().id(id++).build();
        users.put(user.getId(), user);
        log.info("Добавлен объект пользователя {}", user);

        return user.getId();
    }

    @Override
    public Long update(User obj) {
        long idForUser = obj.getId();
        if (!users.containsKey(idForUser)) {
            throw new UpdateException("Ошибка обновления, пользователя нет в списке:" + obj);
        }
        users.put(idForUser, obj);
        log.info("Обновлен объект пользователь: {}", obj);
        return obj.getId();
    }

    @Override
    public boolean delete(Long id) {
        return users.remove(id) != null;
    }

    @Override
    public List<User> getFriends(Long id) {
        return List.of();
    }

    @Override
    public List<User> getCommonFriendsUsers(Long userId1, Long userId2) {
        return List.of();
    }
}
