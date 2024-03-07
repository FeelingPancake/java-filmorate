package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UpdateException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private static long ID = 1;
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        return films.get(id);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody @NonNull Film filmFromRequest) {
        if (films.containsKey(filmFromRequest.getId())) {
            throw new AlreadyExistsException("Фильм уже есть в списке" + filmFromRequest);
        }

        Film film = filmFromRequest.toBuilder().id(ID++).build();
        films.put(film.getId(), film);
        log.info("Добавлен объект фильма {}", film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody @NonNull Film filmFromRequest) {
        if (films.containsKey(filmFromRequest.getId())) {
            films.put(filmFromRequest.getId(), filmFromRequest);
            log.info("Обновлен или добавлен объект фильма: {}", filmFromRequest);
            return filmFromRequest;
        } else {
            throw new UpdateException("Ошибка обновления, фильма нет в списке:" + filmFromRequest);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("Ошибка валидации: {}", errors);

        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AlreadyExistsException.class)
    public Map<String, String> handleFilmExistsExceptions(AlreadyExistsException ex) {
        Map<String, String> errors = new HashMap<>();
        log.warn("Ошибка добавления: {}", ex.getMessage());
        errors.put(ex.toString(), ex.getMessage());

        return errors;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UpdateException.class)
    public Map<String, String> handleUpdateExceptions(UpdateException ex) {
        Map<String, String> errors = new HashMap<>();
        log.warn("Ошибка обновления: {}", ex.getMessage());
        errors.put(ex.toString(), ex.getMessage());

        return errors;
    }

}
