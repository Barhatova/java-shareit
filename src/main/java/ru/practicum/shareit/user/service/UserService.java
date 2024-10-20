package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(Integer userId, NewUserRequest request);

    void deleteUserById(Integer userId);

    List<User> getAllUser();

    User getUserById(Integer userId);
}
