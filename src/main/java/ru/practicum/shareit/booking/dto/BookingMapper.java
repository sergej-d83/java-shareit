package ru.practicum.shareit.booking.dto;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(new BookingDto.User(booking.getId()))
                .item(new BookingDto.Item(booking.getItem().getId(), booking.getItem().getName()))
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return new Booking(bookingDto.getStart(), bookingDto.getEnd(), new Item(), new User());
    }
}
