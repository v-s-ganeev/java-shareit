package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.BookingDtoOutputToOwner;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    public static BookingDtoOutput toBookingDto(Booking booking) {
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

    public static BookingDtoOutputToOwner toBookingDtoToOwner(Booking booking) {
        return BookingDtoOutputToOwner
                .builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static BookingDtoInput toBookingDtoInput(Booking booking) {
        return BookingDtoInput
                .builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .build();
    }

    public static Booking toBooking(BookingDtoOutput bookingDtoOutput) {
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

    public static Booking toBooking(BookingDtoInput bookingDtoInput, User booker, Item item) {
        return Booking
                .builder()
                .start(bookingDtoInput.getStart())
                .end(bookingDtoInput.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static List<BookingDtoOutput> toBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

}
