package ru.yandex.practicum.filmorate.storage.interfacesDao;

import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.ModelStorage;

import java.util.List;


public interface FilmStorage extends ModelStorage<Film> {
    public List<Film> getAll(int limit);
    public void likeFilm(Long userId, Long filmId) throws IdNotFoundException;
    public void dislikeFilm(Long userId, Long filmId) throws IdNotFoundException;
}
