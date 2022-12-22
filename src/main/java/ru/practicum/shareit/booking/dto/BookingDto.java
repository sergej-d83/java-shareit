package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
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

    private Item item;

    private User booker;

    private Status status;

    @Data
    public static class Item {
        private final Long id;

        private final String name;
    }

    @Data
    public static class User {

        private final Long id;
    }
}
