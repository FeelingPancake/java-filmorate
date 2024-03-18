package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.UpdateException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users;
    private long id = 1;

    public InMemoryUserStorage() {
        this.users = new HashMap<>();
    }

    @Override
    public User get(Long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(id.toString());
        }
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        // Не вижу особенного смысла делать копию в ArrayList
        // так как объекты передаются по ссылке и в любом случае передам именно эти ссылки
        return users.values();
    }

    @Override
    public User add(User obj) {
        User user = obj.toBuilder().id(id++).build();
        users.put(user.getId(), user);
        log.info("Добавлен объект пользователя {}", user);

        return user;
    }

    @Override
    public User update(User obj) {
        long idForUser = obj.getId();
        if (!users.containsKey(idForUser)) {
            throw new UpdateException("Ошибка обновления, пользователя нет в списке:" + obj);
        }
        users.put(idForUser, obj);
        log.info("Обновлен объект пользователь: {}", obj);
        return obj;
    }

    @Override
    public User delete(Long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(id.toString());
        }
        User deletedUser = users.get(id);
        users.remove(id);
        return deletedUser;
    }
}
