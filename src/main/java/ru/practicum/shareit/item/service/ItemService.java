package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Integer userId);

    ItemDto editItem(ItemDto itemDto, Integer itemId, Integer userId);

    ItemDto getItem(Integer itemId);

    List<ItemDto> getUserItems(Integer userId);

    List<ItemDto> getNeedItems(String searchString);

}
