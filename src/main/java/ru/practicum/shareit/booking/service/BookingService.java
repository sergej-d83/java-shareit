package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long userId, BookingDto bookingDto);

    BookingDto setApproval(Long bookingId, Long userId, Boolean approved);

    BookingDto findBookingById(Long bookingId, Long userId);

    List<BookingDto> findBookingsOfUser(Long userId, String state);

    List<BookingDto> findBookingsOfOwner(Long ownerId, String state);
}
