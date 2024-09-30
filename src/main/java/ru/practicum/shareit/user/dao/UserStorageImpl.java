package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Integer, User> users;

    public UserStorageImpl() {
        users = new HashMap<>();
    }

    @Override
    public User createUser(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(Integer userId) {
        users.remove(userId);
    }

    @Override
    public Collection<User> getAllUser() {
        return users.values();
    }

    @Override
    public Optional<User> getUserById(Integer userId) {
        if (users.containsKey(userId)) {
            return Optional.of(users.get(userId));
        }
        return Optional.empty();
    }

    private Integer getId() {
        return users.keySet().stream().max(Integer::compareTo).map(aInteger -> aInteger + 1).orElse(Math.toIntExact(1L));
    }
}
