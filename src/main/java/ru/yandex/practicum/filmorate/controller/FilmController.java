package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotations.Marker;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@Validated
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmStorage.get(id);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmStorage.getAll();
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public Film addFilm(@Valid @RequestBody Film filmFromRequest) {
        return filmStorage.add(filmFromRequest);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public Film updateFilm(@Valid @RequestBody Film filmFromRequest) {
        return filmStorage.update(filmFromRequest);
    }

    //PUT /films/{id}/like/{userId}
    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable(value = "id") Long id,
                         @PathVariable(value = "userId") Long userId) {
        filmService.likeFilm(id, userId);

        return filmStorage.get(id);
    }

    //DELETE /films/{id}/like/{userId}
    @DeleteMapping("/{id}/like/{userId}")
    public Film dislikeFilm(@PathVariable(value = "id") Long id,
                            @PathVariable(value = "userId") Long userId) {
        filmService.dislikeFilm(id, userId);

        return filmStorage.get(id);
    }

    //GET /films/popular?count={count}
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}
