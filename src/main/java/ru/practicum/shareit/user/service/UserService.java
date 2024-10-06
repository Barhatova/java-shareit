package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    UserDto createUser(NewUserRequest newUserRequest);

    UserDto updateUser(int userId, User user);

    void deleteUser(int userId);

    Collection<UserDto> getAllUser();

    UserDto getUserById(int userId);
}
