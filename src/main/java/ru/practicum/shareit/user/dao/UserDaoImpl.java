package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserDaoImpl implements UserDao {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(int userId) {
        return users.get(userId);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User updateUser(User user, int userId) {
        users.put(userId, user);
        return users.get(userId);
    }

    @Override
    public void deleteUser(int userId) {
        users.remove(userId);
    }
}
