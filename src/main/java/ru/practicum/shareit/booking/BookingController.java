package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutput addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody BookingDtoInput bookingDtoInput) {
        return bookingService.addBooking(bookingDtoInput, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutput approveBooking(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer bookingId, @RequestParam(name = "approved") Boolean isApproved) {
        return bookingService.approveBooking(bookingId, userId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutput getBookingToOwnerOrBooker(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer bookingId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoOutput> getBookerBookings(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getBookerBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutput> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookings(userId, state);
    }
}
