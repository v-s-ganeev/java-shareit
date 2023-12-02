package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item addItem(Item item);

    Item editItem(Item item);

    void deleteItem(Integer itemId);

    Item getItem(Integer itemId);

    List<Item> getAllItem();
}
