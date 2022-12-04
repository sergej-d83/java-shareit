package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User addUser(User user);

    User getUser(int userId);

    List<User> getAllUsers();

    User updateUser(User user, int userId);

    void deleteUser(int userId);
}
