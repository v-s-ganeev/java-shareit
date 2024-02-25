package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid ItemRequestRequestDto requestDto) {
        return itemRequestClient.addItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getMyItemRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.getMyItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAlItemRequests(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestClient.getAlItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
