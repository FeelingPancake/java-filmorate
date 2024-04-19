package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;

import java.util.Collection;

public interface ModelStorage<T> {
    T get(Long id);

    Collection<T> getAll();

    Long add(T obj);

    Long update(T obj);

    boolean delete(Long id);
}
