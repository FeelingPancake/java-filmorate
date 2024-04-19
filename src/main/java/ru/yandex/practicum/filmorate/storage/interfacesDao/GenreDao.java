package ru.yandex.practicum.filmorate.storage.interfacesDao;

import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {
    public List<Genre> get(Long generalId);
    public boolean add(Long film, Long genreId);
    public boolean add(Long filmId, Long... genreId);
    public boolean delete(Long filmid, Genre genre);
    public List<Long> getGenresIdForFilm(Long filmId);
    boolean deleteAllGenresOfFilm(Long filmId) throws IdNotFoundException;
    Long addNewGenre(String name);
    List<Genre> getAllGenres();
    Genre getGenreById(long genreId);
}
