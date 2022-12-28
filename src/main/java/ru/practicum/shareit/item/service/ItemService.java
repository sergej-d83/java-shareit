package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId);

    ItemDto getItem(Long itemId, Long userId);

    List<ItemDto> getItemsByUserId(Long userId, int from, int size);

    List<ItemDto> searchItems(String text, int from, int size);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}
