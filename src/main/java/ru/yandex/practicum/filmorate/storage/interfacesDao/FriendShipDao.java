package ru.yandex.practicum.filmorate.storage.interfacesDao;

import ru.yandex.practicum.filmorate.model.FriendShip;

public interface FriendShipDao extends RelationDao<FriendShip> {
    public boolean deleteAllFriends(Long userId);
}
