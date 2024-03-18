package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface ModelStorage<T> {
    T get(Long id);

    Collection<T> getAll();

    T add(T obj);

    T update(T obj);

    T delete(Long id);
}
