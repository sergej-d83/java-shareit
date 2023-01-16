package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
public class UserUpdateDto {

    private Long id;

    private String name;

    @Email(message = "Неправильный формат электронной почты.")
    private String email;
}
