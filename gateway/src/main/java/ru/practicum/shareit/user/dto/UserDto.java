package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым.")
    private String name;

    @Email(message = "Неправильный формат электронной почты.")
    @NotNull(message = "Адрес электронной почты не задан.")
    private String email;
}
