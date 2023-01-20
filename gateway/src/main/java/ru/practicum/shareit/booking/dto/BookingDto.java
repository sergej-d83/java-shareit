package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingDto {

    private Long id;

    @FutureOrPresent(message = "Стартовая дата брони не может быть в прошлом.")
    @NotNull
    private LocalDateTime start;

    @Future(message = "Конечная дата брони должна быть в будущем.")
    @NotNull
    private LocalDateTime end;

    private Long itemId;
}
