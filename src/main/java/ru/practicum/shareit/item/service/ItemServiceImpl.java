package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.UserNotOwnItem;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

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
        }
        if (!isUserPresent(userId)) {
            throw new UserNotFoundException("Пользователь под номером " + userId + " не найден.");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет под номером " + itemId + " не найден."));

        updateItemData(itemDto, item, userId);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет под номером " + itemId + " не найден."));

        List<ItemDto.Comment> comments = commentRepository.findByItemId(itemId)
                .stream().map(CommentMapper::toItemComment).collect(Collectors.toList());

        if (item.getOwner().equals(userId)) {
            Booking lastBooking = getLastBooking(itemId);
            Booking nextBooking = getNextBooking(itemId);

            ItemDto itemDto = ItemMapper.toItemDto(item);

            if (lastBooking != null) {
                itemDto.setLastBooking(new ItemDto.Booking(lastBooking.getId(), lastBooking.getBooker().getId()));
            }
            if (nextBooking != null) {
                itemDto.setNextBooking(new ItemDto.Booking(nextBooking.getId(), nextBooking.getBooker().getId()));
            }
            if (!comments.isEmpty()) {
                itemDto.setComments(comments);
            }

            return itemDto;
        }

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(comments);
        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        if (!isUserPresent(userId)) {
            throw new UserNotFoundException("Пользователь с номером " + userId + " не найден.");
        }

        List<ItemDto> items = itemRepository.findAll()
                .stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());


        for (ItemDto item : items) {

            Booking lastBooking = getLastBooking(item.getId());
            Booking nextBooking = getNextBooking(item.getId());

            if (lastBooking != null) {
                item.setLastBooking(new ItemDto.Booking(lastBooking.getId(), lastBooking.getBooker().getId()));
            }
            if (nextBooking != null) {
                item.setNextBooking(new ItemDto.Booking(nextBooking.getId(), nextBooking.getBooker().getId()));
            }

            item.setComments(commentRepository.findByItemId(item.getId())
                    .stream()
                    .map(CommentMapper::toItemComment)
                    .collect(Collectors.toList()));
        }
        return items;
    }

    @Override
    public List<ItemDto> searchItems(String text) {

        if (text.isEmpty()) {
            return Collections.emptyList();
        }

        List<Item> items = itemRepository.searchItems(text);

        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет под номером " + itemId + " не найден."));

        List<Booking> bookings = bookingRepository
                .findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now())
                .stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .collect(Collectors.toList());

        if (bookings.size() != 0) {

            Comment comment = CommentMapper.toComment(commentDto, item, bookings.get(0).getBooker());

            return CommentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new InvalidDataException("Пользователь не бронировал эту вещь.");
        }
    }

    private boolean isUserPresent(Long userId) {
        return userRepository.findAll().stream().anyMatch(user -> Objects.equals(user.getId(), userId));
    }

    private void itemValidation(ItemDto itemDto, Long userId) {
        if (userId <= 0) {
            throw new InvalidDataException("Неправильный номер пользователя: " + userId);
        }
        if (!isUserPresent(userId)) {
            throw new UserNotFoundException("Пользователь под номером " + userId + " не найден.");
        }

        if (itemDto.getAvailable() == null) {
            throw new InvalidDataException("Статус доступности предмета к аренде не указан.");
        }
        if (itemDto.getName().isBlank()) {
            throw new InvalidDataException("Название предмета не указанно.");
        }
        if (itemDto.getDescription() == null) {
            throw new InvalidDataException("Описание предмета не указанно.");
        }
    }

    private void updateItemData(ItemDto itemDto, Item item, Long userId) {
        if (!userId.equals(item.getOwner())) {
            throw new UserNotOwnItem("Предмет номер " + item.getId() + " не принадлежит пользователю номер " + userId);
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
        if (itemDto.getRequest() != null) {
            item.setRequest(itemDto.getRequest());
        }
    }

    private Booking getLastBooking(Long itemId) {

        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        LocalDateTime time = LocalDateTime.now();
        return bookingRepository.findFirstByItem_IdAndStartIsBefore(itemId, time, sort).orElse(null);
    }

    private Booking getNextBooking(Long itemId) {

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime time = LocalDateTime.now();
        return bookingRepository.findFirstByItem_IdAndStartIsAfter(itemId, time, sort).orElse(null);
    }
}
