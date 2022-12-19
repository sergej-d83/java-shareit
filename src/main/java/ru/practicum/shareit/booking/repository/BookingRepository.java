package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "select * from bookings b " +
            "join items i on i.id = b.item_id " +
            "where b.id = ?1 and (booker_id = ?2 or owner_id = ?2)", nativeQuery = true)
    Optional<Booking> findBookingByBookerOrOwner(Long bookingId, Long userId);

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId);

    @Query(value = "select * from bookings b " +
            "join items i on i.id = b.item_id " +
            "where b.booker_id = ?1 and (?2 between b.start_date and b.end_date) " +
            "order by b.start_date desc", nativeQuery = true)
    List<Booking> findAllByBooker_IdAndStatusCurrent(Long userId, LocalDateTime localDateTime);

    List<Booking> findAllByBooker_IdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime localDateTime);

    List<Booking> findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime localDateTime);

    @Query(value = "select * from bookings b " +
            "join items i on i.id = b.item_id " +
            "where status like ?2 and booker_id = ?1 " +
            "order by b.start_date", nativeQuery = true)
    List<Booking> findAllByBooker_IdAndStatus(Long userId, String status);

    List<Booking> findAllByItem_OwnerOrderByStartDesc(Long ownerId);

    @Query(value = "select * from bookings b " +
            "join items i on i.id = b.item_id " +
            "where i.owner_id = ?1 and (?2 between start_date and end_date) " +
            "order by b.start_date desc", nativeQuery = true)
    List<Booking> findAllByItem_OwnerAndStatusCurrent(Long ownerId, LocalDateTime localDateTime);

    List<Booking> findAllByItem_OwnerAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime localDateTime);

    List<Booking> findAllByItem_OwnerAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime localDateTime);

    @Query(value = "select * from bookings b " +
            "join items i on i.id = b.item_id " +
            "where status like ?2 and owner_id = ?1 " +
            "order by b.start_date", nativeQuery = true)
    List<Booking> findAllByItem_OwnerAndState(Long ownerId, String state);
}
