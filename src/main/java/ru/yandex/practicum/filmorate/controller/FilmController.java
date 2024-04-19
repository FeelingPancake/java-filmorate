package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotations.Marker;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmService.getFilm(id);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public Film addFilm(@Valid @RequestBody Film filmFromRequest) {
        Long id = filmService.addFilm(filmFromRequest);
        return filmService.getFilm(id);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public Film updateFilm(@Valid @RequestBody Film filmFromRequest) {
        Long id = filmService.updateFilm(filmFromRequest);
        return filmService.getFilm(id);
    }

    //PUT /films/{id}/like/{userId}
    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable(value = "id") Long id,
                         @PathVariable(value = "userId") Long userId) {
        filmService.likeFilm(userId, id);
    }

    //DELETE /films/{id}/like/{userId}
    @DeleteMapping("/{id}/like/{userId}")
    public void dislikeFilm(@PathVariable(value = "id") Long id,
                            @PathVariable(value = "userId") Long userId) {
        filmService.dislikeFilm(userId, id);
    }

    //GET /films/popular?count={count}
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}
