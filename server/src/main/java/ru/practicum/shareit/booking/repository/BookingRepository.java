package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByStatusAndItem_Owner_idOrderByStartDesc(BookingStatus bookingStatus, Integer ownerId, PageRequest pageRequest);

    List<Booking> findAllByStatusAndBooker_idOrderByStartDesc(BookingStatus bookingStatus, Integer bookerId, PageRequest pageRequest);

    List<Booking> findAllByItem_Owner_idOrderByStartDesc(Integer ownerId, PageRequest pageRequest);

    List<Booking> findAllByBooker_idOrderByStartDesc(Integer bookerId, PageRequest pageRequest);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN b.item AS i " +
            "WHERE i.owner = ?1 " +
            "AND b.start <= ?2 " +
            "AND b.end > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getCurrentBookingToOwner(User owner, LocalDateTime now, PageRequest pageRequest);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker = ?1 " +
            "AND b.start <= ?2 " +
            "AND b.end > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getCurrentBookingToBooker(User booker, LocalDateTime now, PageRequest pageRequest);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN b.item AS i " +
            "WHERE i.owner = ?1 " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getPastBookingToOwner(User owner, LocalDateTime now, PageRequest pageRequest);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker = ?1 " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getPastBookingToBooker(User booker, LocalDateTime now, PageRequest pageRequest);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN b.item AS i " +
            "WHERE i.owner = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getFutureBookingToOwner(User owner, LocalDateTime now, PageRequest pageRequest);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> getFutureBookingToBooker(User booker, LocalDateTime now, PageRequest pageRequest);

    Booking findFirstByItemIdAndStatusNotLikeAndStartIsBeforeOrStartEqualsOrderByStartDesc(Integer itemId, BookingStatus bookingStatus, LocalDateTime now, LocalDateTime now2);

    Booking findFirstByItemIdAndStatusNotLikeAndStartIsAfterOrderByStart(Integer itemId, BookingStatus bookingStatus, LocalDateTime now);

    Booking findFirstByItemIdAndBooker_IdAndStatusNotLikeAndEndIsBeforeOrderByStartDesc(Integer itemId, Integer userId, BookingStatus bookingStatus, LocalDateTime now);


}
