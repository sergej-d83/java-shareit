package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.UserNotOwnItem;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {

        itemValidation(itemDto, userId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        if (itemId <= 0 || userId <= 0) {
            throw new InvalidDataException("Номер предмета и пользователя должны быть больше нуля.");
        } else if (!isUserPresent(userId)) {
            throw new UserNotFoundException("Пользователь под номером " + userId + " не найден.");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет под номером " + itemId + " не найден."));

        updateItemData(itemDto, item, userId);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(Long itemId) {

        return ItemMapper.toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет под номером " + itemId + " не найден.")));
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        if (!isUserPresent(userId)) {
            throw new UserNotFoundException("Пользователь с номером " + userId + " не найден.");
        }

        return itemRepository.findAll()
                .stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {

        return itemRepository.searchItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private boolean isUserPresent(Long userId) {
        return userRepository.findAll().stream().anyMatch(user -> Objects.equals(user.getId(), userId));
    }

    private void itemValidation(ItemDto itemDto, Long userId) {
        if (userId <= 0) {
            throw new InvalidDataException("Неправильный номер пользователя: " + userId);
        } else if (!isUserPresent(userId)) {
            throw new UserNotFoundException("Пользователь под номером " + userId + " не найден.");
        }

        if (itemDto.getIsAvailable() == null) {
            throw new InvalidDataException("Статус доступности предмета к аренде не указан.");
        } else if (itemDto.getName().isBlank()) {
            throw new InvalidDataException("Название предмета не указанно.");
        } else if (itemDto.getDescription() == null) {
            throw new InvalidDataException("Описание предмета не указанно.");
        }
    }

    private void updateItemData(ItemDto itemDto, Item item, Long userId) {
        if (!userId.equals(item.getOwner())) {
            throw new UserNotOwnItem("Предмет номер " + item.getId() + " не принадлежит пользователю номер " + userId);
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        } else if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        } else if (itemDto.getIsAvailable() != null) {
            item.setIsAvailable(itemDto.getIsAvailable());
        } else if (itemDto.getRequest() != null) {
            item.setRequest(itemDto.getRequest());
        }
    }
}
