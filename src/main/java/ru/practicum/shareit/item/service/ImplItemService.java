package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImplItemService implements ItemService {

    @Qualifier("InMemoryItemStorage")
    private final ItemStorage itemStorage;
    @Qualifier("InMemoryUserStorage")
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(ItemDto itemDto, Integer userId) {
        userStorage.getUser(userId);
        if (itemDto.getName() == null || itemDto.getName().isBlank() || itemDto.getDescription() == null || itemDto.getDescription().isBlank() || itemDto.getAvailable() == null) {
            throw new ValidationException("Поля Name, Description и Available обязательны к заполнению");
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        return ItemMapper.toItemDto(itemStorage.addItem(item));
    }

    @Override
    public ItemDto editItem(ItemDto itemDto, Integer itemId, Integer userId) {
        userStorage.getUser(userId);
        if (itemStorage.getItem(itemId).getOwnerId() != userId)
            throw new NotFoundException("Вещь может редактировать только ее владелец");
        itemDto.setId(itemId);
        return ItemMapper.toItemDto(itemStorage.editItem(ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        return ItemMapper.toItemDto(itemStorage.getItem(itemId));
    }

    @Override
    public List<ItemDto> getUserItems(Integer userId) {
        List<ItemDto> userItems = new ArrayList<>();
        List<Item> items = itemStorage.getAllItem();
        for (Item item : items) {
            if (item.getOwnerId() == userId) {
                userItems.add(ItemMapper.toItemDto(item));
            }
        }
        return userItems;
    }

    @Override
    public List<ItemDto> getNeedItems(String searchString) {
        if (searchString.isBlank()) return new ArrayList<>();
        return itemStorage.getAllItem()
                .stream()
                .filter(item -> item.getAvailable()
                        && (item.getName().toLowerCase().contains(searchString.toLowerCase())
                        || item.getDescription().toLowerCase().contains(searchString.toLowerCase())))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
