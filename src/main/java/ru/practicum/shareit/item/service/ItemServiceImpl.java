package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {

        itemValidation(itemDto, userId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);

        if (itemDto.getRequestId() != null) {
            item.setRequest(requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new RequestNotFoundException("Запрос не найден.")));
        }

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

        List<Comment> comments = commentRepository.findByItemId(itemId);

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
                itemDto.setComments(comments.stream().map(CommentMapper::toItemComment).collect(Collectors.toList()));
            }

            return itemDto;
        }

        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (!comments.isEmpty()) {
            itemDto.setComments(comments.stream().map(CommentMapper::toItemComment).collect(Collectors.toList()));
        }

        return itemDto;
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId, int from, int size) {
        if (!isUserPresent(userId)) {
            throw new UserNotFoundException("Пользователь с номером " + userId + " не найден.");
        }

        List<Item> items = itemRepository.findAllByOwner(userId, PageRequest.of(from, size));
        List<Comment> comments = commentRepository.findAllByItem_Owner(userId);
        List<Booking> bookings = bookingRepository.findAllByItem_OwnerAndState(
                userId,
                Status.APPROVED.toString(),
                PageRequest.of(from, size)
        );

        List<ItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {

            Booking lastBooking = bookings.stream()
                    .filter(b -> b.getItem().equals(item))
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now())).max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            Booking nextBooking = bookings.stream()
                    .filter(b -> b.getItem().equals(item))
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now())).min(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            ItemDto itemDto = ItemMapper.toItemDto(item);

            if (lastBooking != null) {
                itemDto.setLastBooking(new ItemDto.Booking(lastBooking.getId(), lastBooking.getBooker().getId()));
            }
            if (nextBooking != null) {
                itemDto.setNextBooking(new ItemDto.Booking(nextBooking.getId(), nextBooking.getBooker().getId()));
            }
            if (!comments.isEmpty()) {
                itemDto.setComments(comments.stream()
                        .filter(c -> c.getItem().getId().equals(item.getId()))
                        .map(CommentMapper::toItemComment)
                        .collect(Collectors.toList()));
            }

            itemDtos.add(itemDto);
        }
        return itemDtos;
    }

    @Override
    public List<ItemDto> searchItems(String text, int from, int size) {

        if (text.isEmpty()) {
            return Collections.emptyList();
        }

        List<Item> items = itemRepository.searchItems(text, PageRequest.of(from, size));

        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмет под номером " + itemId + " не найден."));

        List<Booking> bookings = bookingRepository
                .findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, itemId, LocalDateTime.now());

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
