package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UpdateUserRequest;

import java.util.Collection;

public interface UserService {

    User createUser(User user);

    User updateUser(Integer userId, UpdateUserRequest request);

    void deleteUserById(Integer userId);

    Collection<User> getAllUser();

    User getUserById(Integer userId);
}