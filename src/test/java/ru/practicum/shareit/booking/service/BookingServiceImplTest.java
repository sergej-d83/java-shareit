package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.UserOwnItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    BookingService bookingService;

    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setup() {

        user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("user@user.com");

        item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("test item");
        item.setAvailable(true);
        item.setOwner(1L);
        item.setRequest(new ItemRequest(1L, "test", user, LocalDateTime.now()));

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
    }


    @Test
    void createBookingTest() {

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        bookingService.createBooking(2L, BookingMapper.toBookingDto(booking));

        Throwable throwableOwner = assertThrows(UserOwnItemException.class,
                () -> bookingService.createBooking(1L, BookingMapper.toBookingDto(booking)));
        assertEquals("Пользователь не может арендовать свою вещь", throwableOwner.getMessage());

        booking.setStart(LocalDateTime.now().plusDays(3));

        Throwable throwableDate = assertThrows(InvalidDataException.class,
                () -> bookingService.createBooking(2L, BookingMapper.toBookingDto(booking)));
        assertEquals("Время начала брони должно быть раньше окончания брони.", throwableDate.getMessage());

        item.setAvailable(false);
        Throwable throwableAvailable = assertThrows(ItemNotAvailableException.class,
                () -> bookingService.createBooking(2L, BookingMapper.toBookingDto(booking)));
        assertEquals("Вещь не доступна для аренды", throwableAvailable.getMessage());

    }

    @Test
    void setApprovalTest() {

        Mockito
                .when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        Mockito
                .when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        bookingService.setApproval(1L, 2L, true);
        assertEquals("APPROVED", booking.getStatus().name());

        bookingService.setApproval(1L, 2L, false);
        assertEquals("REJECTED", booking.getStatus().name());

        Throwable wrongUserId = assertThrows(BookingNotFoundException.class,
                () -> bookingService.setApproval(1L, 1L, true));
        assertEquals("Только владелец может менять статус брони.", wrongUserId.getMessage());

        booking.setStatus(Status.APPROVED);
        Throwable alreadyApproved = assertThrows(InvalidDataException.class,
                () -> bookingService.setApproval(1L, 2L, true));
        assertEquals("Бронь уже подтверждена.", alreadyApproved.getMessage());


    }

    @Test
    void findBookingByIdTest() {

        Mockito
                .when(bookingRepository.findBookingByBookerOrOwner(anyLong(), anyLong()))
                .thenAnswer(idCheck -> {
                    Long bookingId = idCheck.getArgument(0, Long.class);

                    if (bookingId <= 0) {
                        throw new BookingNotFoundException("Бронь не найдена.");
                    } else {
                        return Optional.ofNullable(booking);
                    }
                });

        BookingDto bookingDto = bookingService.findBookingById(1L, 1L);

        assertThat(bookingDto.getId(), equalTo(booking.getId()));
        assertThat(bookingDto.getItemId(), equalTo(booking.getItem().getId()));
        assertThat(bookingDto.getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(bookingDto.getItem().getName(), equalTo(booking.getItem().getName()));

        Throwable throwable = assertThrows(BookingNotFoundException.class,
                () -> bookingService.findBookingById(0L, 1L));
        assertEquals("Бронь не найдена.", throwable.getMessage());
    }

    @Test
    void findBookingsOfUserTest() {

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        bookingService.findBookingsOfUser(1L, State.ALL.name(), Pageable.unpaged());
        bookingService.findBookingsOfUser(1L, State.WAITING.name(), Pageable.unpaged());
        bookingService.findBookingsOfUser(1L, State.FUTURE.name(), Pageable.unpaged());
        bookingService.findBookingsOfUser(1L, State.CURRENT.name(), Pageable.unpaged());
        bookingService.findBookingsOfUser(1L, State.REJECTED.name(), Pageable.unpaged());
        bookingService.findBookingsOfUser(1L, State.PAST.name(), Pageable.unpaged());

        Throwable throwable = assertThrows(InvalidDataException.class,
                () -> bookingService.findBookingsOfOwner(1L, State.UNSUPPORTED_STATUS.name(), Pageable.unpaged()));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", throwable.getMessage());
    }

    @Test
    void findBookingsOfOwnerTest() {

        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        bookingService.findBookingsOfOwner(1L, State.ALL.name(), Pageable.unpaged());
        bookingService.findBookingsOfOwner(1L, State.WAITING.name(), Pageable.unpaged());
        bookingService.findBookingsOfOwner(1L, State.FUTURE.name(), Pageable.unpaged());
        bookingService.findBookingsOfOwner(1L, State.CURRENT.name(), Pageable.unpaged());
        bookingService.findBookingsOfOwner(1L, State.REJECTED.name(), Pageable.unpaged());
        bookingService.findBookingsOfOwner(1L, State.PAST.name(), Pageable.unpaged());

        Throwable throwable = assertThrows(InvalidDataException.class,
                () -> bookingService.findBookingsOfOwner(1L, State.UNSUPPORTED_STATUS.name(), Pageable.unpaged()));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", throwable.getMessage());
    }
}