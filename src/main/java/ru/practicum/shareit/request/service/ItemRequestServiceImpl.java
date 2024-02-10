package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestorId(userId);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getMyItemRequests(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
        List<ItemRequestDto> itemRequests = ItemRequestMapper.toItemRequestDto(itemRequestRepository.findAllByRequestorIdOrderByCreated(userId));
        return itemRequests.stream()
                .map(this::addSuggestedItemsToItemRequest)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Integer userId, PageRequest pageRequest) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
        List<ItemRequestDto> itemRequests = ItemRequestMapper.toItemRequestDto(itemRequestRepository.findAllByRequestorIdIsNotOrderByCreated(userId, pageRequest));
        return itemRequests.stream()
                .map(this::addSuggestedItemsToItemRequest)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Integer requestId, Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
        return addSuggestedItemsToItemRequest(ItemRequestMapper.toItemRequestDto(itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос вещи с id = " + requestId + " не найден."))));
    }

    private ItemRequestDto addSuggestedItemsToItemRequest(ItemRequestDto itemRequestDto) {
        itemRequestDto.setItems(ItemMapper.toItemDto(itemRepository.findAllByRequestId(itemRequestDto.getId())));
        return itemRequestDto;
    }
}
