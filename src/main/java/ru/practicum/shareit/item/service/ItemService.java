package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, int userId);

    ItemDto updateItem(ItemDto itemDto, int itemId, int userId);

    ItemDto getItem(int itemId);

    List<ItemDto> getItemsByUserId(int userId);

    List<ItemDto> searchItems(String text);
}
