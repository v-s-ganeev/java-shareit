package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Integer userId);

    CommentDto addComment(CommentDto commentDto, Integer userId, Integer itemId);

    ItemDto editItem(ItemDto itemDto, Integer itemId, Integer userId);

    ItemDto getItem(Integer itemId, Integer userId);

    List<ItemDto> getUserItems(Integer userId, PageRequest pageRequest);

    List<ItemDto> getNeedItems(String searchString, PageRequest pageRequest);

}
