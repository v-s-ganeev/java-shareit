package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
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

    @Override
    public BookingDtoOutput addBooking(BookingDtoInput bookingDtoInput, Integer bookerId) {
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("Пользователь с id=" + bookerId + " не найден"));
        Item item = itemRepository.findById(bookingDtoInput.getItemId()).orElseThrow(() -> new NotFoundException("Вещь с id=" + bookingDtoInput.getItemId() + " не найдена"));
        checkBooking(bookingDtoInput, bookerId, item);
        Booking booking = BookingMapper.toBooking(bookingDtoInput, booker, item);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
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
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOutput getBooking(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId)
            throw new NotFoundException("Смотреть бронирование может только бронирующий и владелец вещи");
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDtoOutput> getOwnerBookings(Integer ownerId, String state, PageRequest pageRequest) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь с id= " + ownerId + " не найден."));
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.getCurrentBookingToOwner(owner, LocalDateTime.now(), pageRequest);
                break;
            case "PAST":
                bookings = bookingRepository.getPastBookingToOwner(owner, LocalDateTime.now(), pageRequest);
                break;
            case "FUTURE":
                bookings = bookingRepository.getFutureBookingToOwner(owner, LocalDateTime.now(), pageRequest);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByStatusAndItem_Owner_idOrderByStartDesc(BookingStatus.WAITING, ownerId, pageRequest);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByStatusAndItem_Owner_idOrderByStartDesc(BookingStatus.REJECTED, ownerId, pageRequest);
                break;
            case "ALL":
                bookings = bookingRepository.findAllByItem_Owner_idOrderByStartDesc(ownerId, pageRequest);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return BookingMapper.toBookingDto(bookings);
    }

    @Override
    public List<BookingDtoOutput> getBookerBookings(Integer bookerId, String state, PageRequest pageRequest) {
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("Пользователь с id= " + bookerId + " не найден."));
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.getCurrentBookingToBooker(booker, LocalDateTime.now(), pageRequest);
                break;
            case "PAST":
                bookings = bookingRepository.getPastBookingToBooker(booker, LocalDateTime.now(), pageRequest);
                break;
            case "FUTURE":
                bookings = bookingRepository.getFutureBookingToBooker(booker, LocalDateTime.now(), pageRequest);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByStatusAndBooker_idOrderByStartDesc(BookingStatus.WAITING, bookerId, pageRequest);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByStatusAndBooker_idOrderByStartDesc(BookingStatus.REJECTED, bookerId, pageRequest);
                break;
            case "ALL":
                bookings = bookingRepository.findAllByBooker_idOrderByStartDesc(bookerId, pageRequest);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return BookingMapper.toBookingDto(bookings);
    }

    private void checkBooking(BookingDtoInput bookingDtoInput, Integer bookerId, Item item) {
        if (item.getOwner().getId() == bookerId)
            throw new NotFoundException("Нельзя забронировать собственную вещь");
        if (!item.getAvailable())
            throw new ValidationException("Владелец вещи закрыл ее для броинрования.");
        if (bookingDtoInput.getStart().isBefore(LocalDateTime.now()))
            throw new ValidationException("Дата начала бронирования не может быть в прошлом");
        if (bookingDtoInput.getStart().isAfter(bookingDtoInput.getEnd()) || bookingDtoInput.getStart().isEqual(bookingDtoInput.getEnd()))
            throw new ValidationException("Дата начала бронирования должна быть раньше времени окончания бронирования.");
    }
}
