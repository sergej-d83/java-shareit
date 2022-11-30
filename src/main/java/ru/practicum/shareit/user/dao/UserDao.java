package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserDao {
    User addUser(User user);
    User getUser(int userId);
    Collection<User> getAllUsers();
    User updateUser(User user, int userId);
    void deleteUser(int userId);
}
