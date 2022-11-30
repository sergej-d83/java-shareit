package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    private int id = 0;

    public UserDto addUser(UserDto userDto) {

        if (isEmailPresent(userDto)) {
            throw new EmailAlreadyExistsException("Пользователь с такой почтой уже существует. " + userDto.getEmail());
        } else if (userDto.getEmail() == null) {
            throw new InvalidDataException("Электронная почта не указана.");
        }

        userDto.setId(generateId());
        User user = UserMapper.toUser(userDto);

        return UserMapper.toUserDto(userDao.addUser(user));
    }

    public UserDto getUser(int userId) {

        if (!isUserIdPresent(userId)) {
            throw new UserNotFoundException("Пользователь под номером " + userId + " не найден.");
        }

        return UserMapper.toUserDto(userDao.getUser(userId));
    }

    public Collection<UserDto> getAllUsers() {
        return userDao.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto updateUser(UserDto userDto, int userId) {
        if (!isUserIdPresent(userId)) {
            throw new UserNotFoundException("Пользователь под номером " + userId + " не найден.");
        }
        if (isEmailPresent(userDto)) {
            throw new EmailAlreadyExistsException("Пользователь с такой почтой уже существует. " + userDto.getEmail());
        }

        User user = UserMapper.toUser(getUser(userId));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        return UserMapper.toUserDto(userDao.updateUser(user, userId));
    }

    public void deleteUser(int userId) {
        userDao.deleteUser(userId);
    }

    private int generateId() {
        return ++id;
    }

    private boolean isUserIdPresent(int userId) {
        return userDao.getAllUsers().stream().anyMatch(user -> user.getId() == userId);
    }

    private boolean isEmailPresent(UserDto userDto) {
        return userDao.getAllUsers().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()));
    }
}
