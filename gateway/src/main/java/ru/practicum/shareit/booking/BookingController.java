package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookerBookings(@RequestHeader("X-Sharer-User-Id") int userId,
													@RequestParam(name = "state", defaultValue = "all") String stateParam,
													@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
													@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));
		log.info("Get booker bookings with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookerBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") int userId,
			@RequestBody @Valid BookingRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.addBooking(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingToOwnerOrBooker(@RequestHeader("X-Sharer-User-Id") int userId,
			@PathVariable Integer bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBookingToOwnerOrBooker(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") int userId,
												 @PathVariable int bookingId,
												 @RequestParam(name = "approved") Boolean isApproved) {
		log.info("Approved bookingId {}, userId {}", bookingId, isApproved);
		return bookingClient.approveBooking(userId, bookingId, isApproved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") int userId,
												   @RequestParam(name = "state", defaultValue = "all") String stateParam,
												   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
												   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new ValidationException("Unknown state: " + stateParam));
		log.info("Get owner bookings with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getOwnerBookings(userId, state, from, size);
	}
}
