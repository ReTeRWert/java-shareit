package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // а все запросы надо править или только один?
    @Query(value = "SELECT * " +
            "FROM bookings AS b " +
            "WHERE b.item_id = ?1 " +
            "AND b.start_date < ?2 " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date DESC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Booking> getItemLastBooking(Long itemId, LocalDateTime localDateTime);

    @Query(value = "SELECT * " +
            "FROM bookings AS b " +
            "WHERE b.item_id = ?1 " +
            "AND b.start_date > ?2 " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date ASC " +
            "LIMIT 1", nativeQuery = true)
    Optional<Booking> getItemNextBooking(Long itemId, LocalDateTime localDateTime);

    List<Booking> findAllByBookerIdIsOrderByIdDesc(Long id);

    @Query(" SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?2 " +
            "ORDER BY b.id ASC")
    List<Booking> findAllCurrentBookings(Long id, LocalDateTime now);

    @Query(" SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.id DESC")
    List<Booking> findAllFutureBookings(Long id, LocalDateTime now);

    @Query(" SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.end < ?2 " +
            "ORDER BY b.id DESC")
    List<Booking> findAllPastBookings(Long id, LocalDateTime now);

    @Query(" SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.status = 'WAITING' " +
            "ORDER BY b.id DESC")
    List<Booking> findAllWaitingBookings(Long id);

    @Query(" SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.id DESC")
    List<Booking> findAllRejectedBookings(Long id);

    @Query(" SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "ORDER BY b.id DESC")
    List<Booking> findAllOwnerBookings(Long userId);

    @Query(" SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?2 " +
            "ORDER BY b.id DESC")
    List<Booking> findAllOwnerCurrentBookings(Long userId, LocalDateTime now);

    @Query(" SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.id DESC")
    List<Booking> findAllOwnerFutureBookings(Long userId, LocalDateTime now);

    @Query(" SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.end < ?2 " +
            "ORDER BY b.id DESC")
    List<Booking> findAllOwnerPastBookings(Long userId, LocalDateTime now);

    @Query(" SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.status = 'WAITING' " +
            "ORDER BY b.id DESC")
    List<Booking> findAllOwnerWaitingBookings(Long userId);

    @Query(" SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.id DESC")
    List<Booking> findAllOwnerRejectedBookings(Long userId);
}
