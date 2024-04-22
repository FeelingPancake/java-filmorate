package ru.yandex.practicum.filmorate.service;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.SqlExecuteException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.interfacesDao.FilmDao;
import ru.yandex.practicum.filmorate.storage.interfacesDao.GenreDao;
import ru.yandex.practicum.filmorate.storage.interfacesDao.LikeDao;
import ru.yandex.practicum.filmorate.storage.interfacesDao.MpaDao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmDao filmDao;
    private final LikeDao likeDao;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;

    public FilmService(FilmDao filmDao, LikeDao likeDao, GenreDao genreDao, MpaDao mpaDao) {
        this.filmDao = filmDao;
        this.likeDao = likeDao;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
    }

    public Film getFilm(Long id) {
        Film film = filmDao.get(id);

        return film.toBuilder().genres(genreDao.get(id)).build();

    }

    public Collection<Film> getFilms() {
        List<Film> films = (List<Film>) filmDao.getAll();
        Map<Long, List<Genre>> genres = genreDao.getAll();

        Map<Long, List<Genre>> genresMap = genreDao.getAll()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return films.stream()
                .map(film -> film.toBuilder().genres(genresMap.getOrDefault(film.getId(), List.of())).build())
                .collect(Collectors.toList());
    }

    public Film addFilm(Film film) {

        try {
            mpaDao.get(film.getMpa().id());
        } catch (DataAccessException e) {
            throw new SqlExecuteException(e.getMessage());
        }
        Long id = filmDao.add(film);

        if (!(film.getGenres() == null) && !film.getGenres().isEmpty()) {
            genreDao.add(id, film.getGenres());
        }

        return film.toBuilder().id(id).genres(genreDao.get(id)).build();
    }

    public Long updateFilm(Film film) {
        if (!(film.getGenres() == null) && !film.getGenres().isEmpty()) {
            genreDao.update(film.getId(), film.getGenres());
        }

        return filmDao.update(film);
    }

    public void likeFilm(Long userId, Long filmId) {
        likeDao.add(new Like(userId, filmId));
    }

    public void dislikeFilm(Long filmId, Long userId) {
        likeDao.delete(new Like(userId, filmId));
    }

    public List<Film> getPopularFilms(int count) {
        return filmDao.getPopular(count);
    }
}
