package ru.yandex.practicum.filmorate.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String mes) {
        super("Пользователь по id:" + mes + " не найден.");
    }
}
