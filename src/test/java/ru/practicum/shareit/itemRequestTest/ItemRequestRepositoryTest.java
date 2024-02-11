package ru.practicum.shareit.itemRequestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository repository;

    ItemRequest itemRequest = ItemRequest.builder().description("description").created(LocalDateTime.now()).requestorId(1).build();

    @BeforeEach
    void setUp() {
        em.persist(itemRequest);
    }

    @Test
    void findAllByRequestorIdOrderByCreated() {
        List<ItemRequest> itemRequests = repository.findAllByRequestorIdOrderByCreated(1);

        assertNotNull(itemRequests);
        assertEquals(itemRequests.size(), 1);
        assertEquals(itemRequests.get(0), itemRequest);
    }
}
