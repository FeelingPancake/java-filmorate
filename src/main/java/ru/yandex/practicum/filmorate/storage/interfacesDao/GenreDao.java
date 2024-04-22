package ru.yandex.practicum.filmorate.storage.interfacesDao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;

public interface GenreDao {
    List<Genre> get(Long generalId);

    Map<Long, List<Genre>> getAll();

    boolean add(Long filmId, List<Genre> genreId);

    boolean update(Long filmId, List<Genre> genre);

    boolean delete(Long filmId, Long genreId);

    List<Genre> getGenreForId(Long genreId);

    List<Long> getGenresIdForFilm(Long filmId);

    Long addNewGenre(String name);

    List<Genre> getAllGenres();

}
