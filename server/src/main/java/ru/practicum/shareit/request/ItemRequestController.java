package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getMyItemRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getMyItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAlItemRequests(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(value = "from", defaultValue = "0") Integer from, @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getAllItemRequests(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer requestId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}
