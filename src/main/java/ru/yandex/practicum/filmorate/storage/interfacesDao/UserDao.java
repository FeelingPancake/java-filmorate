package ru.yandex.practicum.filmorate.storage.interfacesDao;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.ModelStorage;

import java.util.List;


public interface UserDao extends ModelStorage<User> {
    List<User> getFriends(Long id);

    List<User> getCommonFriendsUsers(Long userId1, Long userId2);
}
