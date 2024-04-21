package ru.yandex.practicum.filmorate.storage.interfacesDao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {
    List<Genre> get(Long generalId);

    boolean add(Long filmId, List<Genre> genreId);

    boolean delete(Long filmId, Long genreId);

    List<Genre> getGenreForId(Long genreId);

    List<Long> getGenresIdForFilm(Long filmId);

    Long addNewGenre(String name);

    List<Genre> getAllGenres();

}
