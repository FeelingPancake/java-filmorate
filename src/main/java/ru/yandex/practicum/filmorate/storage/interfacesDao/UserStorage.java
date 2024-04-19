package ru.yandex.practicum.filmorate.storage.interfacesDao;

import ru.yandex.practicum.filmorate.model.FriendShip;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.ModelStorage;


public interface UserStorage extends ModelStorage<User> {
    boolean addFriend(long userId, long friendId);
    boolean deleteFriend(FriendShip friendShip);
}
