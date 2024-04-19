package ru.yandex.practicum.filmorate.storage.interfacesDao;

import ru.yandex.practicum.filmorate.model.Like;

public interface LikeDao extends RelationDao<Like> {
    public boolean deleteAllLikesToFilm(Long filmId);
}
