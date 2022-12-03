package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.UserNotOwnItem;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private int id = 0;

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public ItemDto addItem(ItemDto itemDto, int userId) {
        if (userId <= 0) {
            throw new InvalidDataException("Неправильный номер пользователя: " + userId);
        } else if (!isUserPresent(userId)) {
            throw new UserNotFoundException("Пользователь под номером " + userId + " не найден.");
        }

        if (itemDto.getAvailable() == null) {
            throw new InvalidDataException("Статус доступности предмета к аренде не указан.");
        } else if (itemDto.getName().isBlank()) {
            throw new InvalidDataException("Название предмета не указанно.");
        } else if (itemDto.getDescription() == null) {
            throw new InvalidDataException("Описание предмета не указанно.");
        }

        Item item = ItemMapper.toItem(itemDto);
        item.setId(generateId());
        item.setOwner(userId);

        return ItemMapper.toItemDto(itemDao.addItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int itemId, int userId) {
        if (itemId <= 0 || userId <= 0) {
            throw new InvalidDataException("Номер предмета и пользователя должны быть больше нуля.");
        } else if (!isItemPresent(itemId)) {
            throw new ItemNotFoundException("Предмет под номером " + itemId + " не найден.");
        } else if (!isUserPresent(userId)) {
            throw new UserNotFoundException("Пользователь под номером " + userId + " не найден.");
        }

        Item item = itemDao.getItem(itemId);

        if (userId != item.getOwner()) {
            throw new UserNotOwnItem("Предмет номер " + itemId + " не принадлежит пользователю номер " + userId);
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemDao.updateItem(item, itemId));
    }

    @Override
    public ItemDto getItem(int itemId) {
        if (!isItemPresent(itemId)) {
            throw new ItemNotFoundException("Предмет под номером " + itemId + " не найден.");
        }
        return ItemMapper.toItemDto(itemDao.getItem(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUserId(int userId) {
        if (!isUserPresent(userId)) {
            throw new UserNotFoundException("Пользователь с номером " + userId + " не найден.");
        }
        return itemDao.getItemsByUserId(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {

        return itemDao.searchItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private int generateId() {
        return ++id;
    }

    private boolean isUserPresent(int userId) {
        return userDao.getAllUsers().stream().anyMatch(user -> user.getId() == userId);
    }

    private boolean isItemPresent(int itemId) {
        return itemDao.getItems().stream().anyMatch(item -> item.getId() == itemId);
    }
}
