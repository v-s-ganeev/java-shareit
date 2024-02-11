package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Integer userId);

    List<ItemRequestDto> getMyItemRequests(Integer userId);

    List<ItemRequestDto> getAllItemRequests(Integer userId, PageRequest pageRequest);

    ItemRequestDto getItemRequestById(Integer requestId, Integer userId);
}
