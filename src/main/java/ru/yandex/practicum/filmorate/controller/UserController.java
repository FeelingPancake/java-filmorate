package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotations.Marker;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userStorage.get(id);
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userStorage.getAll();
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public User addUser(@Valid @RequestBody User userFromRequest) {
        User user;
        String nameUserFromRequest = userFromRequest.getName();
        String loginUserFromRequest = userFromRequest.getLogin();

        if ((nameUserFromRequest == null) || nameUserFromRequest.isBlank()) {
            user = userFromRequest.toBuilder().name(loginUserFromRequest).build();
        } else {
            user = userFromRequest.toBuilder().name(nameUserFromRequest).build();
        }

        return userStorage.add(user);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public User updateUser(@Valid @RequestBody User userFromRequest) {
        return userStorage.update(userFromRequest);
    }

    //PUT /users/{id}/friends/{friendId}
    @PutMapping("/{id}/friends/{friendId}")
    public List<User> addFriend(@PathVariable(value = "id") Long id,
                                @PathVariable(value = "friendId") Long friendId) {
        userService.addFriend(id, friendId);
        List<User> friendList = new ArrayList<>();

        for (Long friendsId : userService.getFriendList(id)) {
            friendList.add(userStorage.get(friendsId));
        }
        return friendList;
    }

    //DELETE /users/{id}/friends/{friendId}
    @DeleteMapping("/{id}/friends/{friendId}")
    public List<User> deleteFriend(@PathVariable(value = "id") Long id,
                                   @PathVariable(value = "friendId") Long friendId) {
        userService.deleteFriend(id, friendId);
        List<User> friendList = new ArrayList<>();

        for (Long friendsId : userService.getFriendList(id)) {
            friendList.add(userStorage.get(friendsId));
        }
        return friendList;

    }

    //GET /users/{id}/friends
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable(value = "id") Long id) {
        List<User> friendList = new ArrayList<>();

        for (Long friendId : userService.getFriendList(id)) {
            friendList.add(userStorage.get(friendId));
        }
        return friendList;
    }

    //GET /users/{id}/friends/common/{otherId}
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable(value = "id") Long id,
                                       @PathVariable(value = "otherId") Long otherId) {
        List<User> commonFriendList = new ArrayList<>();

        for (Long friendId : userService.findCommonFriends(id, otherId)) {
            commonFriendList.add(userStorage.get(friendId));
        }
        return commonFriendList;
    }
}
