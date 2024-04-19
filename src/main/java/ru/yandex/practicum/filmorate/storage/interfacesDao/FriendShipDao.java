package ru.yandex.practicum.filmorate.storage.interfacesDao;

import ru.yandex.practicum.filmorate.model.FriendShip;

public interface FriendShipDao extends RelationDao<FriendShip> {
    boolean deleteAllFriends(Long userId);
}
