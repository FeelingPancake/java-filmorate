package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FriendShip;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfacesDao.UserStorage;

import java.util.Collection;
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

    public Long addUser(User user) {
        return userStorage.add(user);
    }

    public Long updateUser(User user) {
        return userStorage.update(user);
    }

    public List<User> getFriendList(long userId) {
        return userStorage.get(userId).getFriendsList().stream()
                .map(x -> {
                    if (x.userId() == userId) {
                       return userStorage.get(x.friendId());
                    } else {
                        return userStorage.get(x.userId());
                    }
                })
                .collect(Collectors.toList());
    }

    public void addFriend(long userId, long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        for(FriendShip friendShip : userStorage.get(userId).getFriendsList()) {
            if (friendShip.userId() == userId && friendShip.friendId() == friendId) {
                userStorage.deleteFriend(friendShip);
            }
        }
    }

    public List<User> findCommonFriends(long userId1, long userId2) {
        User user1 = userStorage.get(userId1);
        User user2 = userStorage.get(userId2);

        Set<Long> commonFriends = user1.getFriendsList().stream().map(FriendShip::friendId).collect(Collectors.toSet());
        commonFriends.retainAll(user2.getFriendsList().stream().map(FriendShip::friendId).collect(Collectors.toList()));

        return commonFriends.stream()
                .map(userStorage::get)
                .collect(Collectors.toList());
    }

}
