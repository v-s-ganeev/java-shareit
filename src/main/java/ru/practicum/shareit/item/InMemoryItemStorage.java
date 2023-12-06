package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryItemStorage implements ItemStorage {

    private int id = 0;
    private Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item editItem(Item item) {
        Item itemInDb = getItem(item.getId());
        if (item.getName() != null && !item.getName().isBlank()) itemInDb.setName(item.getName());
        if (item.getDescription() != null && !item.getDescription().isBlank())
            itemInDb.setDescription(item.getDescription());
        if (item.getAvailable() != null) itemInDb.setAvailable(item.getAvailable());
        return itemInDb;
    }

    @Override
    public void deleteItem(Integer itemId) {
        items.remove(itemId);
    }

    @Override
    public Item getItem(Integer itemId) {
        Item item = items.get(itemId);
        if (item == null) throw new NotFoundException("Вещь с id=" + itemId + " не найдена");
        return item;
    }

    @Override
    public List<Item> getAllItem() {
        return new ArrayList<>(items.values());
    }
}
