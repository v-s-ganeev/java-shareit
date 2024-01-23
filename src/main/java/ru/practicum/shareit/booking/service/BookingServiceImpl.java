package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDtoOutput addBooking(BookingDtoInput bookingDtoInput, Integer bookerId) {
        Booking booking = bookingMapper.toBooking(bookingDtoInput, bookerId);
        checkBooking(booking);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOutput approveBooking(Integer bookingId, Integer userId, Boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Подтверждать бронирование может только владелец вещи");
        }
        if (booking.getStatus() == BookingStatus.APPROVED || booking.getStatus() == BookingStatus.REJECTED)
            throw new ValidationException("Ответ на данное бронирование уже дан.");
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOutput getBooking(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId)
            throw new NotFoundException("Смотреть бронирование может только бронирующий и владелец вещи");
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDtoOutput> getOwnerBookings(Integer ownerId, String state) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь с id= " + ownerId + " не найден."));
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.getCurrentBookingToOwner(owner, LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.getPastBookingToOwner(owner, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.getFutureBookingToOwner(owner, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByStatusAndItem_Owner_idOrderByStartDesc(BookingStatus.WAITING, ownerId);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByStatusAndItem_Owner_idOrderByStartDesc(BookingStatus.REJECTED, ownerId);
                break;
            case "ALL":
                bookings = bookingRepository.findAllByItem_Owner_idOrderByStartDesc(ownerId);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookingMapper.toBookingDto(bookings);
    }

    @Override
    public List<BookingDtoOutput> getBookerBookings(Integer bookerId, String state) {
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("Пользователь с id= " + bookerId + " не найден."));
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.getCurrentBookingToBooker(booker, LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.getPastBookingToBooker(booker, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.getFutureBookingToBooker(booker, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByStatusAndBooker_idOrderByStartDesc(BookingStatus.WAITING, bookerId);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByStatusAndBooker_idOrderByStartDesc(BookingStatus.REJECTED, bookerId);
                break;
            case "ALL":
                bookings = bookingRepository.findAllByBooker_idOrderByStartDesc(bookerId);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookingMapper.toBookingDto(bookings);
    }

    private void checkBooking(Booking booking) {
        if (booking.getItem().getOwner().getId() == booking.getBooker().getId())
            throw new NotFoundException("Нельзя забронировать собственную вещь");
        if (!booking.getItem().getAvailable())
            throw new ValidationException("Владелец вещи закрыл ее для броинрования.");
        if (booking.getStart().isBefore(LocalDateTime.now()))
            throw new ValidationException("Дата начала бронирования не может быть в прошлом");
        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isEqual(booking.getEnd()))
            throw new ValidationException("Дата начала бронирования должна быть раньше времени окончания бронирования.");
    }
}
