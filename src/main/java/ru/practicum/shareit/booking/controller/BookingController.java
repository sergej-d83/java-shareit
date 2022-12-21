package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    public static final String USER_ID = "X-Sharer-User-id";

    @PostMapping
    public BookingDto createBooking(@RequestHeader(USER_ID) Long userId, @Valid @RequestBody BookingDto bookingDto) {

        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApproval(@RequestParam Boolean approved,
                                  @PathVariable Long bookingId,
                                  @RequestHeader(USER_ID) Long userId) {

        return bookingService.setApproval(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@PathVariable Long bookingId, @RequestHeader(USER_ID) Long userId) {

        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingsOfOwner(@RequestHeader(USER_ID) Long userId,
                                                @RequestParam(value = "state", defaultValue = "ALL") String state) {

        return bookingService.findBookingsOfOwner(userId, state);
    }

    @GetMapping
    public List<BookingDto> findBookingsOfUser(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                               @RequestHeader(USER_ID) Long userId) {

        return bookingService.findBookingsOfUser(userId, state);
    }
}
