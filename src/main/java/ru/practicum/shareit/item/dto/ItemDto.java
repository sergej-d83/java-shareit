package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private ItemRequest request;

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

    public ItemDto(Long id, String name, String description, Boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
        this.comments = Collections.emptyList();
    }
}
