package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public BookingDto createBooking(Long userId, BookingDto bookingDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь под номером " + userId + " не найден."));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Предмет под номером " + bookingDto.getItemId() + " не найден."));

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);

        if (item.getOwner().equals(userId)) {
            throw new UserOwnItemException("Пользователь не может арендовать свою вещь");
        }
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Вещь не доступна для аренды");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new InvalidDataException("Время начала брони должно быть раньше окончания брони.");
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto setApproval(Long bookingId, Long userId, Boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронь не найдена."));

        if (!booking.getBooker().getId().equals(userId)) {
            if (approved && !booking.getStatus().equals(Status.APPROVED)) {
                booking.setStatus(Status.APPROVED);
            } else if (!approved && !booking.getStatus().equals(Status.REJECTED)) {
                booking.setStatus(Status.REJECTED);
            } else {
                throw new InvalidDataException("Бронь уже подтверждена.");
            }
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new BookingNotFoundException("Только владелец может менять статус брони.");
        }
    }

    @Override
    public BookingDto findBookingById(Long bookingId, Long userId) {
        return BookingMapper.toBookingDto(bookingRepository.findBookingByBookerOrOwner(bookingId, userId)
                .orElseThrow(() -> new BookingNotFoundException("Бронь не найдена.")));
    }

    @Override
    public List<BookingDto> findBookingsOfUser(Long userId, String state, Pageable pageable) {

        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь под номером " + userId + " не найден.");
        }

        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByBooker_IdOrderByStartDesc(userId, pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case CURRENT:
                return bookingRepository.findAllByBooker_IdAndStatusCurrent(userId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllByBooker_IdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllByBooker_IdAndStatus(userId, Status.WAITING.toString(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllByBooker_IdAndStatus(userId, Status.REJECTED.toString(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            default:
                throw new InvalidDataException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> findBookingsOfOwner(Long ownerId, String state, Pageable pageable) {

        if (userRepository.findById(ownerId).isEmpty()) {
            throw new UserNotFoundException("Пользователь под номером " + ownerId + " не найден.");
        }

        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByItem_OwnerOrderByStartDesc(ownerId, pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case CURRENT:
                return bookingRepository.findAllByItem_OwnerAndStatusCurrent(ownerId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(ownerId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(ownerId, LocalDateTime.now(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllByItem_OwnerAndState(ownerId, State.WAITING.toString(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllByItem_OwnerAndState(ownerId, State.REJECTED.toString(), pageable)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());

            case UNSUPPORTED_STATUS:
                throw new InvalidDataException("Unknown state: UNSUPPORTED_STATUS");
            default:
                return Collections.emptyList();
        }
    }

}
