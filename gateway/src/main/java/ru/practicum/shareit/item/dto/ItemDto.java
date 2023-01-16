package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название вещи не может быть пустым.")
    private String name;

    @NotBlank(message = "Описание вещи не может быть пустым.")
    private String description;

    @NotNull
    private Boolean available;

    private Booking lastBooking;

    private Booking nextBooking;

    private List<Comment> comments;

    private Long requestId;

    @Data
    public static class Booking {

        private final Long id;

        private final Long bookerId;
    }

    @Data
    public static class Comment {

        private final Long id;

        private final String text;

        private final String authorName;

        private final LocalDateTime created;
    }
}
