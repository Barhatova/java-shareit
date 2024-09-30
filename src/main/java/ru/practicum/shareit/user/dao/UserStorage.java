package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Integer userId);

    Collection<User> getAllUser();

    Optional<User> getUserById(Integer userId);
}
