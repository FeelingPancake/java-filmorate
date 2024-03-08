package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UpdateException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private long id = 1;
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return users.get(id);
    }

    @GetMapping
    public Collection<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User userFromRequest) {
        User user;
        String nameUserFromRequest = userFromRequest.getName();
        String loginUserFromRequest = userFromRequest.getLogin();

        if (users.containsKey(userFromRequest.getId())) {
            throw new AlreadyExistsException("Пользователь уже есть в списке:" + userFromRequest);
        }

        if ((nameUserFromRequest == null) || nameUserFromRequest.isBlank()) {
            user = userFromRequest.toBuilder().name(loginUserFromRequest).id(id++).build();
        } else {
            user = userFromRequest.toBuilder().name(nameUserFromRequest).id(id++).build();
        }
        users.put(user.getId(), user);
        log.info("Добавлен объект пользователя {}", user);

        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User userFromRequest) {
        long idForUser = userFromRequest.getId();
        if (!users.containsKey(idForUser)) {
            throw new UpdateException("Пользователь не найден в списке:" + userFromRequest);
        }

        users.put(idForUser, userFromRequest);
        log.info("Обновлен или добавлен объект пользователя: {}", userFromRequest);

        return userFromRequest;
    }
}
