package ru.yandex.practicum.filmorate.exceptions;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String mes) {
        super(mes);
    }
}
