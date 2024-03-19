package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(Long id) {
        return userStorage.get(id);
    }

    public Collection<User> getUsers() {
        return userStorage.getAll();
    }

    public User addUser(User user) {
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public List<User> getFriendList(long userId) {
        return userStorage.get(userId).getFriendsList().stream()
                .map(userStorage::get)
                .collect(Collectors.toList());
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        user.getFriendsList().add(friendId);
        friend.getFriendsList().add(userId);
    }

    public void deleteFriend(long userId, long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        user.getFriendsList().remove(friendId);
        friend.getFriendsList().remove(userId);
    }

    public List<User> findCommonFriends(long userId1, long userId2) {
        User user1 = userStorage.get(userId1);
        User user2 = userStorage.get(userId2);

        Set<Long> commonFriends = new HashSet<>(user1.getFriendsList());
        commonFriends.retainAll(user2.getFriendsList());

        return commonFriends.stream().map(userStorage::get).collect(Collectors.toList());
    }

}
