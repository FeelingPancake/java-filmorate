package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfacesDao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfacesDao.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
    }

    public Film getFilm(Long id) {
        return filmStorage.get(id);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getAll();
    }

    public Long addFilm(Film film) throws IdNotFoundException {
        return filmStorage.add(film);
    }

    public Long updateFilm(Film film) {
        return filmStorage.update(film);
    }

    public void likeFilm(Long userId, Long filmId) {
        try {
            filmStorage.likeFilm(userId, filmId);
        } catch (IdNotFoundException e) {
            throw new FilmNotFoundException(filmId + "");
        }
    }

    public void dislikeFilm(Long filmId, Long userId) {
        try {
            filmStorage.dislikeFilm(filmId, userId);
        } catch (IdNotFoundException e) {
            throw new FilmNotFoundException(filmId + "");
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAll(count);
    }
}
