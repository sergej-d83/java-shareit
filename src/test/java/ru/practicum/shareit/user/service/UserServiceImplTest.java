package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@Transactional
@SpringBootTest
@RequiredArgsConstructor
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserDto userDto;

    @BeforeEach
    void setup() {

        userDto = new UserDto(1L, "user", "user@user.com");
    }

    @Test
    void addUserTest() {

        User user = UserMapper.toUser(userDto);

        Mockito
                .when(userRepository.save(user))
                .thenReturn(user);

        UserDto newUser = userService.addUser(userDto);

        assertEquals(newUser.getId(), userDto.getId());
        assertEquals(newUser.getName(), userDto.getName());
        assertEquals(newUser.getEmail(), userDto.getEmail());
    }

    @Test
    void getUserTest() {

        User user = UserMapper.toUser(userDto);

        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        UserDto newUser = userService.getUser(1L);

        assertEquals(userDto.getId(), newUser.getId());
        assertEquals(userDto.getName(), newUser.getName());
        assertEquals(userDto.getEmail(), newUser.getEmail());

        Throwable throwable = assertThrows(UserNotFoundException.class,
                () -> userService.getUser(2L));
        assertEquals("Пользователь под номером " + 2 + " не найден.", throwable.getMessage());
    }

    @Test
    void getAllUsersTest() {

        User user = UserMapper.toUser(userDto);

        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<UserDto> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals(userDto.getName(), users.get(0).getName());
        assertEquals(userDto.getEmail(), users.get(0).getEmail());
    }

    @Test
    void updateUserTest() {

        User user = UserMapper.toUser(userDto);
        UserDto updatedUser = new UserDto(1L, "updated user", "updateduser@user.com");

        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user);

        UserDto result = userService.updateUser(updatedUser, user.getId());

        assertEquals(result.getId(), updatedUser.getId());
        assertEquals(result.getName(), updatedUser.getName());
        assertEquals(result.getEmail(), updatedUser.getEmail());

        Throwable throwable = assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(updatedUser, 2L));
        assertEquals("Пользователь под номером " + 2 + " не найден.", throwable.getMessage());
    }

    @Test
    void deleteUserTest() {

        Mockito.doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        Mockito.verify(userRepository, times(1)).deleteById(any());
    }
}