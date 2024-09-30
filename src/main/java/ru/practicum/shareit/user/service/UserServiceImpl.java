package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateParameterException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        User user = UserMapper.mapToUser(newUserRequest);
        validateEmail(user);
        return UserMapper.mapToDto(userStorage.createUser(user));
    }

    @Override
    public UserDto updateUser(Integer userId, User user) {
        User oldUser = getUser(userId);
        if (user.getEmail() != null) {
            validateEmail(user);
            oldUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        return UserMapper.mapToDto(userStorage.updateUser(oldUser));
    }

    @Override
    public void deleteUser(Integer userId) {
        userStorage.deleteUser(userId);
    }

    @Override
    public Collection<UserDto> getAllUser() {
        return userStorage.getAllUser().stream()
                .map(UserMapper::mapToDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Integer userId) {
        return UserMapper.mapToDto(getUser(userId));
    }

    private void validateEmail(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@") ||
                !(user.getEmail().matches("^(.+)@(\\S+)$"))) {
            log.warn("Email пользователя введен некорректно {}", user);
            throw new ValidationException("Email пользователя введен некорректно");
        }
        userStorage.getAllUser().stream()
                .filter(user1 -> user1.getEmail().equals(user.getEmail()))
                .findFirst()
                .ifPresent(user1 -> {
                    log.warn("Email уже есть у пользователя {}", user);
                    throw new DuplicateParameterException("Email уже есть");
                });
    }

    private User getUser(Integer userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id {} не найден", userId);
                    return new NotFoundException(String.format("Пользователь с id не найден {}", userId));
                });
    }
}