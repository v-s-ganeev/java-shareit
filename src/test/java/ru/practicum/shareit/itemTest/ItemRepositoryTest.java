package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository repository;

    User owner = User.builder().name("user").email("user@user.ru").build();
    Item item = Item.builder().name("item").description("itemDescription").owner(owner).available(true).build();
    PageRequest pageRequest = PageRequest.of(0, 1);

    @BeforeEach
    void setUp() {
        em.persist(owner);
        em.persist(item);
    }

    @Test
    void searchByText() {
        List<Item> items = repository.searchByText("item", pageRequest);

        assertNotNull(items);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0), item);
    }
}
