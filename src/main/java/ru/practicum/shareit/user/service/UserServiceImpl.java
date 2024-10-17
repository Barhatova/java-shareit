package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        userValidation(user);
        userRepository.save(user);
        return user;
    }

    @Override
    public User updateUser(Integer userId, NewUserRequest request) {
        User updatedUser = getUserById(userId);
        if (request.getEmail() != null) {
            updatedUser.setEmail(request.getEmail());
        }
        UserMapper.updateUserFields(updatedUser, request);
        return userRepository.save(updatedUser);
    }

    @Override
    public void deleteUserById(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователя с таким id не существует");
        }
        return user.get();
    }

    private void userValidation(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя пользователя не задано");
            throw new ValidationException("Имя пользователя не задано");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Не указана электронная почта пользователя");
            throw new ValidationException("Не указана электронная почта пользователя");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Электронная почта не содержит символ - @: {} ", user.getEmail());
            throw new ValidationException("Электронная почта не содержит символ - @");
        }
    }
}