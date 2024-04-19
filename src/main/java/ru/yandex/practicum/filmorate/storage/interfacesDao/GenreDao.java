package ru.yandex.practicum.filmorate.storage.interfacesDao;

import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {
    List<Genre> get(Long generalId);

    boolean add(Long film, Long genreId);

    boolean add(Long filmId, Long... genreId);

    boolean delete(Long filmid, Genre genre);

    List<Long> getGenresIdForFilm(Long filmId);

    boolean deleteAllGenresOfFilm(Long filmId) throws IdNotFoundException;

    Long addNewGenre(String name);

    List<Genre> getAllGenres();

    Genre getGenreById(long genreId);
}
