package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfacesDao.FriendShipDao;
import ru.yandex.practicum.filmorate.storage.interfacesDao.UserDao;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private final UserDao userDao;
    private final FriendShipDao friendDao;

    public UserService(UserDao userDao, FriendShipDao friendDao) {
        this.userDao = userDao;
        this.friendDao = friendDao;
    }

    public User getUser(Long id) {
        return userDao.get(id);
    }

    public Collection<User> getUsers() {
        return userDao.getAll();
    }

    public Long addUser(User user) {
        return userDao.add(user);
    }

    public Long updateUser(User user) {
        return userDao.update(user);
    }

    public List<User> getFriendList(long userId) {
        userDao.get(userId);
        return userDao.getFriends(userId);
    }

    public boolean addFriend(long userId, long friendId) {
        userDao.get(userId);
        userDao.get(friendId);

        return friendDao.add(userId, friendId);
    }

    public boolean deleteFriend(long userId, long friendId) {
        userDao.get(userId);
        userDao.get(friendId);

        return friendDao.delete(userId, friendId);
    }

    public List<User> findCommonFriends(long userId1, long userId2) {
        return friendDao.getCommonFriends(userId1, userId2).stream().map(userDao::get).toList();
    }

}
