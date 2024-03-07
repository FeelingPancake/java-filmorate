package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UpdateException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private static long ID = 1;
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return users.get(id);
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody @NonNull User userFromRequest) {
        User user;

        if (users.containsKey(userFromRequest.getId())) {
            throw new AlreadyExistsException("Пользователь уже есть в списке:" + userFromRequest);
        }

        if (Objects.isNull(userFromRequest.getName()) || userFromRequest.getName().isBlank()) {
            user = userFromRequest.toBuilder().name(userFromRequest.getLogin()).id(ID++).build();
        } else {
            user = userFromRequest.toBuilder().name(userFromRequest.getName()).id(ID++).build();
        }
        users.put(user.getId(), user);
        log.info("Добавлен объект пользователя {}", user);

        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody @NonNull User userFromRequest) {
        if (users.containsKey(userFromRequest.getId())) {
            users.put(userFromRequest.getId(), userFromRequest);
            log.info("Обновлен или добавлен объект пользователя: {}", userFromRequest);

            return userFromRequest;
        } else {
            throw new UpdateException("Пользователь не найден в списке:" + userFromRequest);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("Ошибка валидации: {}", errors);

        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AlreadyExistsException.class)
    public Map<String, String> handleUserExistsExceptions(AlreadyExistsException ex) {
        Map<String, String> errors = new HashMap<>();
        log.warn("Ошибка добавления:{}", ex.getMessage());
        errors.put(ex.toString(), ex.getMessage());

        return errors;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UpdateException.class)
    public Map<String, String> handleUpdateExceptions(UpdateException ex) {
        Map<String, String> errors = new HashMap<>();
        log.warn("Ошибка обновления:{}", ex.getMessage());
        errors.put(ex.toString(), ex.getMessage());

        return errors;
    }
}
