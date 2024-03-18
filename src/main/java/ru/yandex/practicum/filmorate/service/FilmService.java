package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void likeFilm(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId);
        User user = userStorage.get(userId);
        if (film == null) {
            throw new FilmNotFoundException(filmId + "");
        }
        if (user == null) {
            throw new UserNotFoundException(userId + "");
        }
        if (!film.getLikedBy().contains(userId)) {
            film.getLikedBy().add(userId);
            film.setRating(film.getRating() + 1);
        }
    }

    public void dislikeFilm(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId);
        User user = userStorage.get(userId);
        if (film == null) {
            throw new FilmNotFoundException(filmId + "");
        }
        if (user == null) {
            throw new UserNotFoundException(userId + "");
        }
        if (film.getLikedBy().contains(userId)) {
            film.getLikedBy().remove(userId);
            film.setRating(film.getRating() - 1);
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> Integer.compare(f1.getRating(), f2.getRating()) * -1)
                .limit(count)
                .collect(Collectors.toList());
    }
}
