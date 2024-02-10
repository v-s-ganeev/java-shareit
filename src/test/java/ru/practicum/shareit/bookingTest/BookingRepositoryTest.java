package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository repository;

    User user = User.builder().name("user").email("user@user.ru").build();
    User owner = User.builder().name("owner").email("owner@owner.ru").build();
    Item item = Item.builder().name("item").description("description").owner(owner).available(true).build();
    Booking booking = Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().minusDays(1)).item(item).booker(user).build();
    PageRequest pageRequest = PageRequest.of(0, 1);

    @BeforeEach
    void setUp() {
        em.persist(user);
        em.persist(owner);
        em.persist(item);
        em.persist(booking);
    }

    @Test
    public void getPastBookingOwner() {
        List<Booking> bookings = repository.getPastBookingToOwner(owner, LocalDateTime.now(), pageRequest);

        assertNotNull(bookings);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), booking);
    }

}
