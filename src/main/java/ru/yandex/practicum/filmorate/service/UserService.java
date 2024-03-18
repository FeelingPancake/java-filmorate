package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Set<Long> getFriendList(long userId) {
        return userStorage.get(userId).getFriendsList();
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        if (user == null) {
            throw new UserNotFoundException(userId + "");
        }
        if (friend == null) {
            throw new UserNotFoundException(friendId + "");
        }

        user.getFriendsList().add(friendId);
        friend.getFriendsList().add(userId);
    }

    public void deleteFriend(long userId, long friendId) {
        User user = userStorage.get(userId);
        User friend = userStorage.get(friendId);

        if (user == null) {
            throw new UserNotFoundException(userId + "");
        }
        if (friend == null) {
            throw new UserNotFoundException(friendId + "");
        }

        user.getFriendsList().remove(friendId);
        friend.getFriendsList().remove(userId);
    }

    public Set<Long> findCommonFriends(long userId1, long userId2) {
        User user1 = userStorage.get(userId1);
        User user2 = userStorage.get(userId2);

        if (user1 == null) {
            throw new UserNotFoundException(userId1 + "");
        }
        if (user2 == null) {
            throw new UserNotFoundException(userId2 + "");
        }

        Set<Long> commonFriends = new HashSet<>(user1.getFriendsList());
        commonFriends.retainAll(user2.getFriendsList());

        return commonFriends;
    }

}
