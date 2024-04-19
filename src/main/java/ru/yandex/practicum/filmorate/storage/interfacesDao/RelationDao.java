package ru.yandex.practicum.filmorate.storage.interfacesDao;

import java.util.List;

public interface RelationDao<T> {
    List<T> get(Long generalId);

    boolean add(T... relations);

    boolean delete(T relation);
}
