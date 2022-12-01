package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, int userId);

    ItemDto updateItem(ItemDto itemDto, int itemId, int userId);

    ItemDto getItem(int itemId);

    Collection<ItemDto> getItemsByUserId(int userId);

    Collection<ItemDto> searchItems(String text);
}
