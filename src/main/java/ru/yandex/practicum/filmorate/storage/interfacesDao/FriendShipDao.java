package ru.yandex.practicum.filmorate.storage.interfacesDao;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;

public interface FriendShipDao extends RelationDao<Friendship> {
    boolean add(Long userId, Long friendId);

    boolean delete(Long userId, Long friendId);

    boolean update(Friendship friendShip);

    List<Long> getCommonFriends(Long userId1, Long userId2);
}
