package ru.yandex.practicum.filmorate.exceptions;

public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(String mes) {
        super("Фильм по id:" + mes + " отсутствует.");
    }
}
