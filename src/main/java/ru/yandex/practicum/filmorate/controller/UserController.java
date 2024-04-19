package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotations.Marker;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsers();
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

        Long id = userService.addUser(user);
        return userService.getUser(id);
    }

    @PutMapping
    @Validated({Marker.OnUpdate.class})
    public User updateUser(@Valid @RequestBody User userFromRequest) {
        Long id = userService.updateUser(userFromRequest);
        return userService.getUser(id);
    }

    //PUT /users/{id}/friends/{friendId}
    @PutMapping("/{id}/friends/{friendId}")
    public List<User> addFriend(@PathVariable(value = "id") Long id,
                                @PathVariable(value = "friendId") Long friendId) {
        userService.addFriend(id, friendId);

        return userService.getFriendList(id);
    }

    //DELETE /users/{id}/friends/{friendId}
    @DeleteMapping("/{id}/friends/{friendId}")
    public List<User> deleteFriend(@PathVariable(value = "id") Long id,
                                   @PathVariable(value = "friendId") Long friendId) {
        userService.deleteFriend(id, friendId);

        return userService.getFriendList(friendId);
    }

    //GET /users/{id}/friends
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable(value = "id") Long id) {

        return userService.getFriendList(id);
    }

    //GET /users/{id}/friends/common/{otherId}
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable(value = "id") Long id,
                                       @PathVariable(value = "otherId") Long otherId) {
        return userService.findCommonFriends(id, otherId);
    }
}
