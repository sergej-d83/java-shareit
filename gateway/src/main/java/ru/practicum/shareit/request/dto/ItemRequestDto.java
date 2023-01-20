package ru.practicum.shareit.request.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    private Long id;

    @NotBlank(message = "Описание пустое.")
    private String description;

    private LocalDateTime created;

    private List<Item> items;

    @Data
    public static class Item {

        private Long id;

        private Long ownerId;

        private String name;

        private String description;

        private Boolean available;

        private Long requestId;
    }
}
