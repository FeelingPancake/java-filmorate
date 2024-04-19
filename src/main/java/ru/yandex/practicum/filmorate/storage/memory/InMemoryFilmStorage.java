package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UpdateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfacesDao.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films;
    private long id = 1;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
    }

    @Override
    public Film get(Long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new FilmNotFoundException(id.toString());
        }
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        // Не вижу особенного смысла делать копию в ArrayList
        // так как объекты передаются по ссылке и в любом случае передам именно эти ссылки
        return films.values();
    }

    @Override
    public Long add(Film obj) {
        Film film = obj.toBuilder().id(id++).build();
        films.put(film.getId(), film);
        log.info("Добавлен объект фильма {}", film);

        return film.getId();
    }

    @Override
    public Long update(Film obj) {
        long idForFilm = obj.getId();
        if (!films.containsKey(idForFilm)) {
            throw new UpdateException("Ошибка обновления, фильма нет в списке:" + obj);
        }
        films.put(idForFilm, obj);
        log.info("Обновлен объект фильма: {}", obj);
        return obj.getId();
    }

    @Override
    public boolean delete(Long id) {
       return films.remove(id) != null;
    }

    @Override
    public List<Film> getAll(int limit) {
        return List.of();
    }

    @Override
    public void likeFilm(Long userId, Long filmId) throws IdNotFoundException {

    }

    @Override
    public void dislikeFilm(Long userId, Long filmId) throws IdNotFoundException {

    }
}
