package ru.yandex.practicum.filmorate.storage.interfacesDao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.ModelStorage;

import java.util.List;


public interface FilmDao extends ModelStorage<Film> {
    List<Film> getPopular(int limit);
}
