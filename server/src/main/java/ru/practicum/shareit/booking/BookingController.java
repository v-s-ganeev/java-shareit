package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

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
    public List<BookingDtoOutput> getBookerBookings(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(name = "state", defaultValue = "ALL") String state, @RequestParam(value = "from", defaultValue = "0") Integer from, @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return bookingService.getBookerBookings(userId, state, PageRequest.of(from / size, size));
    }

    @GetMapping("/owner")
    public List<BookingDtoOutput> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(name = "state", defaultValue = "ALL") String state, @RequestParam(value = "from", defaultValue = "0") Integer from, @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return bookingService.getOwnerBookings(userId, state, PageRequest.of(from / size, size));
    }
}
