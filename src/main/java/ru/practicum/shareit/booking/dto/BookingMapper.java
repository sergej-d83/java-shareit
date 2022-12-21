package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {

        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                new BookingDto.Item(booking.getItem().getId(), booking.getItem().getName()),
                new BookingDto.User(booking.getBooker().getId()),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDto bookingDto) {

        return new Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd());
    }
}
