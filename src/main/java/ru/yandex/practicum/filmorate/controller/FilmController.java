package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UpdateException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private long id = 1;
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        return films.get(id);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film filmFromRequest) {
        if (films.containsKey(filmFromRequest.getId())) {
            throw new AlreadyExistsException("Фильм уже есть в списке" + filmFromRequest);
        }

        Film film = filmFromRequest.toBuilder().id(id++).build();
        films.put(film.getId(), film);
        log.info("Добавлен объект фильма {}", film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film filmFromRequest) {
        long idForFilm = filmFromRequest.getId();
        if (!films.containsKey(idForFilm)) {
            throw new UpdateException("Ошибка обновления, фильма нет в списке:" + filmFromRequest);
        }
        films.put(idForFilm, filmFromRequest);
        log.info("Обновлен или добавлен объект фильма: {}", filmFromRequest);
        return filmFromRequest;
    }
}
