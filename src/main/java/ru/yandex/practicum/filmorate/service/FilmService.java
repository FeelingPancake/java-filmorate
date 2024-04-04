package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
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

    public Film getFilm(Long id) {
        return filmStorage.get(id);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getAll();
    }

    public Film addFilm(Film film) {
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    public void likeFilm(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId);
        User user = userStorage.get(userId);

        film.like(userId);
    }

    public void dislikeFilm(Long filmId, Long userId) {
        Film film = filmStorage.get(filmId);
        User user = userStorage.get(userId);

        film.dislike(userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getRating(), f1.getRating()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
