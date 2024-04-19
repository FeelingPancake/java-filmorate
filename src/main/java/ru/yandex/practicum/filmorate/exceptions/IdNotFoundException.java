package ru.yandex.practicum.filmorate.exceptions;

import java.sql.SQLException;

public class IdNotFoundException extends RuntimeException {
    public IdNotFoundException(Long id) {
        super("Идентификатор:" + id + " не верен.");
    }
}
