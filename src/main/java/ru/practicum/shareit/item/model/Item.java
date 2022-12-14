package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
@AllArgsConstructor
public class Item {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private int owner;

    private ItemRequest request;

    public Item(String name, String description, boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
