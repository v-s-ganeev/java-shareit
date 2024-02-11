package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookingServiceTest {
    @Autowired
    private BookingService service;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    private User user = User.builder().id(1).name("user").email("user@user.ru").build();
    private User owner = User.builder().id(2).name("owner").email("owner@owner.ru").build();
    private Item item = Item.builder().id(1).name("item").description("description").available(true).owner(owner).requestId(1).build();
    private Booking booking = Booking.builder().id(1).start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).item(item).booker(user).status(BookingStatus.WAITING).build();
    private BookingDtoInput bookingDtoInput = BookingDtoInput.builder().itemId(1).start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).build();
    private PageRequest pageRequest = PageRequest.of(0, 10);

    @Test
    void addBooking() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoOutput bookingDtoOutput = service.addBooking(bookingDtoInput, 1);

        assertNotNull(bookingDtoOutput);
        assertEquals(bookingDtoOutput.getItem().getId(), item.getId());
        assertEquals(bookingDtoOutput.getBooker().getId(), user.getId());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void addBookingWithOwnerIdEqualsBookerId() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> service.addBooking(bookingDtoInput, 2));
    }

    @Test
    void approveBookingWithApprovedTrue() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoOutput bookingDtoOutput = service.approveBooking(1, 2, true);

        assertNotNull(bookingDtoOutput);
        assertEquals(bookingDtoOutput.getStatus(), BookingStatus.APPROVED);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approveBookingWithApprovedFalse() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoOutput bookingDtoOutput = service.approveBooking(1, 2, false);

        assertNotNull(bookingDtoOutput);
        assertEquals(bookingDtoOutput.getStatus(), BookingStatus.REJECTED);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void getBooking() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        BookingDtoOutput bookingDtoOutput = service.getBooking(1, 1);

        assertNotNull(bookingDtoOutput);
        verify(bookingRepository, times(1)).findById(anyInt());
    }

    @Test
    void getBookingWithUserNotOwnerAndNotBooker() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> service.getBooking(1, 3));
    }

    @Test
    void getOwnerBookingsWithStateCurrent() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.getCurrentBookingToOwner(any(User.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(booking));

        List<BookingDtoOutput> bookings = service.getOwnerBookings(1, "CURRENT", pageRequest);

        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        verify(bookingRepository, times(1)).getCurrentBookingToOwner(any(User.class), any(LocalDateTime.class), any(PageRequest.class));
    }

    @Test
    void getOwnerBookingsWithStatePast() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.getPastBookingToOwner(any(User.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(booking));

        List<BookingDtoOutput> bookings = service.getOwnerBookings(1, "PAST", pageRequest);

        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        verify(bookingRepository, times(1)).getPastBookingToOwner(any(User.class), any(LocalDateTime.class), any(PageRequest.class));
    }

    @Test
    void getOwnerBookingsWithStateFuture() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.getFutureBookingToOwner(any(User.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(booking));

        List<BookingDtoOutput> bookings = service.getOwnerBookings(1, "FUTURE", pageRequest);

        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        verify(bookingRepository, times(1)).getFutureBookingToOwner(any(User.class), any(LocalDateTime.class), any(PageRequest.class));
    }

    @Test
    void getOwnerBookingsWithStateWaiting() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByStatusAndItem_Owner_idOrderByStartDesc(any(BookingStatus.class), anyInt(), any(PageRequest.class))).thenReturn(List.of(booking));

        List<BookingDtoOutput> bookings = service.getOwnerBookings(1, "WAITING", pageRequest);

        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        verify(bookingRepository, times(1)).findAllByStatusAndItem_Owner_idOrderByStartDesc(any(BookingStatus.class), anyInt(), any(PageRequest.class));
    }

    @Test
    void getOwnerBookingsWithStateRejected() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByStatusAndItem_Owner_idOrderByStartDesc(any(BookingStatus.class), anyInt(), any(PageRequest.class))).thenReturn(List.of(booking));

        List<BookingDtoOutput> bookings = service.getOwnerBookings(1, "REJECTED", pageRequest);

        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        verify(bookingRepository, times(1)).findAllByStatusAndItem_Owner_idOrderByStartDesc(any(BookingStatus.class), anyInt(), any(PageRequest.class));
    }

    @Test
    void getOwnerBookingsWithStateAll() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItem_Owner_idOrderByStartDesc(anyInt(), any(PageRequest.class))).thenReturn(List.of(booking));

        List<BookingDtoOutput> bookings = service.getOwnerBookings(1, "ALL", pageRequest);

        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        verify(bookingRepository, times(1)).findAllByItem_Owner_idOrderByStartDesc(anyInt(), any(PageRequest.class));
    }

    @Test
    void getOwnerBookingsWithStateNotValid() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> service.getOwnerBookings(1, "CURENT", pageRequest));
    }

    @Test
    void getBookerBookingsWithStateCurrent() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.getCurrentBookingToBooker(any(User.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(booking));

        List<BookingDtoOutput> bookings = service.getBookerBookings(1, "CURRENT", pageRequest);

        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        verify(bookingRepository, times(1)).getCurrentBookingToBooker(any(User.class), any(LocalDateTime.class), any(PageRequest.class));
    }

    @Test
    void getBookerBookingsWithStatePast() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.getPastBookingToBooker(any(User.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(booking));

        List<BookingDtoOutput> bookings = service.getBookerBookings(1, "PAST", pageRequest);

        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        verify(bookingRepository, times(1)).getPastBookingToBooker(any(User.class), any(LocalDateTime.class), any(PageRequest.class));
    }

    @Test
    void getBookerBookingsWithStateFuture() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.getFutureBookingToBooker(any(User.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(List.of(booking));

        List<BookingDtoOutput> bookings = service.getBookerBookings(1, "FUTURE", pageRequest);

        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        verify(bookingRepository, times(1)).getFutureBookingToBooker(any(User.class), any(LocalDateTime.class), any(PageRequest.class));
    }

    @Test
    void getBookerBookingsWithStateWaiting() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByStatusAndBooker_idOrderByStartDesc(any(BookingStatus.class), anyInt(), any(PageRequest.class))).thenReturn(List.of(booking));

        List<BookingDtoOutput> bookings = service.getBookerBookings(1, "WAITING", pageRequest);

        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        verify(bookingRepository, times(1)).findAllByStatusAndBooker_idOrderByStartDesc(any(BookingStatus.class), anyInt(), any(PageRequest.class));
    }

    @Test
    void getBookerBookingsWithStateRejected() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByStatusAndBooker_idOrderByStartDesc(any(BookingStatus.class), anyInt(), any(PageRequest.class))).thenReturn(List.of(booking));

        List<BookingDtoOutput> bookings = service.getBookerBookings(1, "REJECTED", pageRequest);

        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        verify(bookingRepository, times(1)).findAllByStatusAndBooker_idOrderByStartDesc(any(BookingStatus.class), anyInt(), any(PageRequest.class));
    }

    @Test
    void getBookerBookingsWithStateAll() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker_idOrderByStartDesc(anyInt(), any(PageRequest.class))).thenReturn(List.of(booking));

        List<BookingDtoOutput> bookings = service.getBookerBookings(1, "ALL", pageRequest);

        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        verify(bookingRepository, times(1)).findAllByBooker_idOrderByStartDesc(anyInt(), any(PageRequest.class));
    }

    @Test
    void getBookerBookingsWithStateNotValid() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> service.getBookerBookings(1, "CURENT", pageRequest));
    }
}
