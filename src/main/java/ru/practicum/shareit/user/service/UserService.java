package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto getUser(int userId);

    Collection<UserDto> getAllUsers();

    UserDto updateUser(UserDto userDto, int userId);

    void deleteUser(int userId);
}
