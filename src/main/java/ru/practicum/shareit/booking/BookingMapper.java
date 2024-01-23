package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.BookingDtoOutputToOwner;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingDtoOutput toBookingDto(Booking booking) {
        return BookingDtoOutput
                .builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public BookingDtoOutputToOwner toBookingDtoToOwner(Booking booking) {
        return BookingDtoOutputToOwner
                .builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public Booking toBooking(BookingDtoOutput bookingDtoOutput) {
        return Booking
                .builder()
                .id(bookingDtoOutput.getId())
                .start(bookingDtoOutput.getStart())
                .end(bookingDtoOutput.getEnd())
                .item(ItemMapper.toItem(bookingDtoOutput.getItem()))
                .booker(UserMapper.toUser(bookingDtoOutput.getBooker()))
                .status(bookingDtoOutput.getStatus())
                .build();
    }

    public Booking toBooking(BookingDtoInput bookingDtoInput, Integer bookerId) {
        return Booking
                .builder()
                .start(bookingDtoInput.getStart())
                .end(bookingDtoInput.getEnd())
                .item(itemRepository.findById(bookingDtoInput.getItemId()).orElseThrow(() -> new NotFoundException("Вещь с id=" + bookingDtoInput.getItemId() + " не найдена")))
                .booker(userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("Пользователь с id=" + bookerId + " не найден")))
                .status(BookingStatus.WAITING)
                .build();
    }

    public List<BookingDtoOutput> toBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(this::toBookingDto)
                .collect(Collectors.toList());
    }
}
