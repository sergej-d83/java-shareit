package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

public class ItemRequestMapper {

    public static ItemRequestDto toRequestDto(ItemRequest itemRequest) {

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());

        return itemRequestDto;
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(itemRequestDto.getCreated());

        return itemRequest;
    }

    public static ItemRequestDto.Item toRequestDtoItem(Item item) {
        ItemRequestDto.Item newItem = new ItemRequestDto.Item();

        newItem.setId(item.getId());
        newItem.setOwnerId(item.getOwner());
        newItem.setName(item.getName());
        newItem.setDescription(item.getDescription());
        newItem.setAvailable(item.getAvailable());
        newItem.setRequestId(item.getRequest().getId());

        return newItem;
    }
}
