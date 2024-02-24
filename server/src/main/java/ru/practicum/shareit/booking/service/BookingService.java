package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;

import java.util.List;

public interface BookingService {

    BookingDtoOutput addBooking(BookingDtoInput bookingDtoInput, Integer userId);

    BookingDtoOutput approveBooking(Integer bookingId, Integer userId, Boolean isApproved);

    BookingDtoOutput getBooking(Integer bookingId, Integer userId);

    List<BookingDtoOutput> getOwnerBookings(Integer ownerId, String state, PageRequest pageRequest);

    List<BookingDtoOutput> getBookerBookings(Integer bookerId, String state, PageRequest pageRequest);

}
