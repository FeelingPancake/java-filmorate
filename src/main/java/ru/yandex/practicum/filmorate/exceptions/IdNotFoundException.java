package ru.yandex.practicum.filmorate.exceptions;

public class IdNotFoundException extends RuntimeException {
    public IdNotFoundException(Long id) {
        super("Идентификатор:" + id + " не верен.");
    }
}
