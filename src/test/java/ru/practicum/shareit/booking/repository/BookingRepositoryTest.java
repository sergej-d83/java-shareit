package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setup() {

        user = new User();
        user.setName("user");
        user.setEmail("user@user.com");

        item = new Item();
        item.setName("item");
        item.setDescription("test item");
        item.setAvailable(true);
        item.setOwner(1L);

        booking = new Booking();
        booking.setItem(item);
        booking.setStart(LocalDateTime.of(2023, 1, 20, 12, 0, 1));
        booking.setEnd(LocalDateTime.of(2023, 1, 21, 12, 0, 1));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
    }

    @Test
    void contextLoads() {
        assertNotNull(entityManager);
        assertNotNull(bookingRepository);
    }

    @Test
    void findBookingByBookerOrOwnerTest() {

        entityManager.persist(user);
        item.setOwner(user.getId());
        entityManager.persist(item);
        entityManager.persist(booking);

        Booking targetBooking = bookingRepository
                .findBookingByBookerOrOwner(booking.getId(), user.getId())
                .orElseThrow();

        assertEquals(booking.getItem().getName(), targetBooking.getItem().getName());
    }

    @Test
    void findAllByBooker_IdOrderByStartDescTest() {

        entityManager.persist(user);
        item.setOwner(user.getId());
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> targetBooking = bookingRepository
                .findAllByBooker_IdOrderByStartDesc(user.getId(), PageRequest.of(0, 1));

        assertEquals(1, targetBooking.size());
        assertEquals(booking.getItem().getName(), targetBooking.get(0).getItem().getName());
        assertEquals(booking.getBooker().getName(), targetBooking.get(0).getBooker().getName());
    }

    @Test
    void findAllByBooker_IdAndStatusCurrentTest() {

        entityManager.persist(user);
        item.setOwner(user.getId());
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> targetBooking = bookingRepository
                .findAllByBooker_IdAndStatusCurrent(
                        user.getId(),
                        LocalDateTime.of(2023, 1, 20, 13, 0, 1),
                        PageRequest.of(0, 1));

        assertEquals(1, targetBooking.size());
        assertEquals(booking.getId(), targetBooking.get(0).getId());
    }

    @Test
    void findAllByBooker_IdAndStartIsAfterOrderByStartDescTest() {

        entityManager.persist(user);
        item.setOwner(user.getId());
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> targetBooking = bookingRepository
                .findAllByBooker_IdAndStartIsAfterOrderByStartDesc(
                        user.getId(),
                        LocalDateTime.now(),
                        PageRequest.of(0, 1));

        assertEquals(1, targetBooking.size());
        assertEquals(booking.getId(), targetBooking.get(0).getId());
        assertEquals(booking.getBooker().getId(), targetBooking.get(0).getBooker().getId());
    }

    @Test
    void findAllByBooker_IdAndEndIsBeforeOrderByStartDescTest() {

        entityManager.persist(user);
        item.setOwner(user.getId());
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> targetBooking = bookingRepository
                .findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(
                        user.getId(),
                        LocalDateTime.of(2023, 1, 23, 12, 0, 1),
                        PageRequest.of(0, 1));

        assertEquals(1, targetBooking.size());
        assertEquals(booking.getId(), targetBooking.get(0).getId());
        assertEquals(booking.getBooker().getId(), targetBooking.get(0).getBooker().getId());
    }

    @Test
    void findAllByBooker_IdAndEndIsBeforeOrderByStartDesc() {

        entityManager.persist(user);
        item.setOwner(user.getId());
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> targetBooking = bookingRepository
                .findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(
                        user.getId(),
                        LocalDateTime.of(2023, 1, 23, 12, 0, 1));

        assertEquals(1, targetBooking.size());
        assertEquals(booking.getId(), targetBooking.get(0).getId());
        assertEquals(booking.getBooker().getId(), targetBooking.get(0).getBooker().getId());
    }

    @Test
    void findAllByBooker_IdAndStatusTest() {

        entityManager.persist(user);
        item.setOwner(user.getId());
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> targetBooking = bookingRepository
                .findAllByBooker_IdAndStatus(user.getId(), "WAITING", PageRequest.of(0, 1));

        assertEquals(1, targetBooking.size());
        assertEquals(booking.getBooker().getId(), targetBooking.get(0).getBooker().getId());
    }

    @Test
    void findAllByItem_OwnerOrderByStartDescTest() {

        entityManager.persist(user);
        item.setOwner(user.getId());
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> targetBooking = bookingRepository
                .findAllByItem_OwnerOrderByStartDesc(item.getOwner(), PageRequest.of(0, 1));

        assertEquals(1, targetBooking.size());
        assertEquals(booking.getItem().getOwner(), targetBooking.get(0).getItem().getOwner());
    }

    @Test
    void findAllByItem_OwnerAndStatusCurrentTest() {

        entityManager.persist(user);
        item.setOwner(user.getId());
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> targetBooking = bookingRepository
                .findAllByItem_OwnerAndStatusCurrent(
                        item.getOwner(),
                        LocalDateTime.of(2023, 1, 20, 23, 0, 1),
                        PageRequest.of(0, 1));

        assertEquals(1, targetBooking.size());
        assertEquals(booking.getItem().getOwner(), targetBooking.get(0).getItem().getOwner());
    }

    @Test
    void findAllByItem_OwnerAndEndIsBeforeOrderByStartDescTest() {

        entityManager.persist(user);
        item.setOwner(user.getId());
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> targetBooking = bookingRepository
                .findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(
                        item.getOwner(),
                        LocalDateTime.of(2023, 1, 23, 12, 0, 1),
                        PageRequest.of(0, 1));

        assertEquals(1, targetBooking.size());
        assertEquals(booking.getItem().getOwner(), targetBooking.get(0).getItem().getOwner());
    }

    @Test
    void findAllByItem_OwnerAndStartIsAfterOrderByStartDescTest() {

        entityManager.persist(user);
        item.setOwner(user.getId());
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> targetBooking = bookingRepository
                .findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(
                        item.getOwner(),
                        LocalDateTime.now(),
                        PageRequest.of(0, 1));

        assertEquals(1, targetBooking.size());
        assertEquals(booking.getItem().getOwner(), targetBooking.get(0).getItem().getOwner());
    }

    @Test
    void findAllByItem_OwnerAndStateTest() {

        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking);

        List<Booking> targetBooking = bookingRepository
                .findAllByItem_OwnerAndState(item.getOwner(), "WAITING", PageRequest.of(0, 1));

        assertEquals(1, targetBooking.size());
        assertEquals(booking.getItem().getOwner(), targetBooking.get(0).getItem().getOwner());
    }

    @Test
    void findFirstByItem_IdAndStartIsBeforeTest() {

        entityManager.persist(user);
        item.setOwner(user.getId());
        entityManager.persist(item);
        entityManager.persist(booking);

        Booking targetBooking = bookingRepository
                .findFirstByItem_IdAndStartIsBefore(
                        item.getId(),
                        LocalDateTime.of(2023, 1, 23, 12, 0, 1),
                        Sort.by(Sort.Direction.DESC, "start"))
                .orElseThrow();

        assertEquals(item.getId(), targetBooking.getItem().getId());
    }

    @Test
    void findFirstByItem_IdAndStartIsAfterTest() {

        entityManager.persist(user);
        item.setOwner(user.getId());
        entityManager.persist(item);
        entityManager.persist(booking);

        Booking targetBooking = bookingRepository
                .findFirstByItem_IdAndStartIsAfter(
                        item.getId(),
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"))
                .orElseThrow();

        assertEquals(item.getId(), targetBooking.getItem().getId());
    }
}