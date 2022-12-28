package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
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
                                                @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                @Positive @RequestParam(defaultValue = "10") int size) {

        int page = from / size;

        return bookingService.findBookingsOfOwner(userId, state, PageRequest.of(page, size));
    }

    @GetMapping
    public List<BookingDto> findBookingsOfUser(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                               @RequestHeader(USER_ID) Long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                               @Positive @RequestParam(defaultValue = "10") int size) {

        int page = from / size;

        return bookingService.findBookingsOfUser(userId, state, PageRequest.of(page, size));
    }
}
