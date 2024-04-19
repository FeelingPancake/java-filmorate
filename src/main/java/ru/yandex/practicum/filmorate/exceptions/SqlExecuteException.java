package ru.yandex.practicum.filmorate.exceptions;

public class SqlExecuteException extends RuntimeException {
    public SqlExecuteException(String mes) {
        super("Запрос:" + mes + " не верен.");
    }
}
