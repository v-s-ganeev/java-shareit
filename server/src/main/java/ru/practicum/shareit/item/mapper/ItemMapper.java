package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item
                .builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .requestId(itemDto.getRequestId())
                .build();
    }

    public static List<ItemDto> toItemDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
