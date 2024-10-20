package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Modifying
    @Query("UPDATE Booking b "
            + "SET b.status = :status  "
            + "WHERE b.id = :bookingId")
    void save(BookingStatus status, Integer bookingId);

    Collection<Booking> getByBookerIdOrderByStartDesc(Integer id);

    Collection<Booking> getByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Integer userId,
                                                                                   LocalDateTime end,
                                                                                   LocalDateTime start);

    Collection<Booking> getByBookerIdAndEndIsBeforeOrderByStartDesc(Integer userId, LocalDateTime now);

    Collection<Booking> getByBookerIdAndStartIsAfterOrderByStartDesc(Integer userId, LocalDateTime now);

    Collection<Booking> getByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(Integer userId,
                                                                                LocalDateTime now,
                                                                                BookingStatus status);

    Collection<Booking> getByBookerIdAndStatusIsOrderByStartDesc(Integer userId, BookingStatus status);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "ORDER BY b.start DESC")
    Collection<Booking> getByItemOwnerId(Integer ownerId);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND :time between b.start AND b.end "
            + "ORDER BY b.start DESC")
    Collection<Booking> getCurrentBookingsOwner(Integer ownerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.end < :time "
            + "ORDER BY b.start DESC")
    Collection<Booking> getPastBookingsOwner(Integer ownerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.start > :time "
            + "ORDER BY b.start DESC")
    Collection<Booking> getFutureBookingsOwner(Integer ownerId, LocalDateTime time);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.start > :time AND b.status = :status "
            + "ORDER BY b.start DESC")
    Collection<Booking> getWaitingBookingsOwner(Integer ownerId, LocalDateTime time, BookingStatus status);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.status = :status "
            + "ORDER BY b.start DESC")
    Collection<Booking> getRejectedBookingsOwner(Integer ownerId, BookingStatus status);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.id = :itemId "
            + "ORDER BY b.start DESC")
    Collection<Booking> getBookingsItem(Integer itemId);

    List<Booking> getByItemIdAndBookerIdAndStatusIsAndEndIsBefore(Integer itemId,
                                                                  Integer bookerId,
                                                                  BookingStatus status,
                                                                  LocalDateTime time);
}
