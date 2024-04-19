package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/genres")
@Validated
public class GenresController {
    private final GenreService genreService;

    public GenresController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> getGenres() {
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable Long id) {
        // На самом деле выглядит небольшим издевательством список id жанров постмана - не соответствует указаному в ТЗ
        return genreService.get(id);
    }

}
