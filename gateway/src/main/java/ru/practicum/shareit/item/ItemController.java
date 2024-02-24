package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getNeedItems(@RequestHeader("X-Sharer-User-Id") Integer userId, String text,
                                               @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemClient.getNeedItems(text, userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid ItemRequestDto requestDto) {
        return itemClient.addItem(userId, requestDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId, @RequestBody @Valid CommentRequestDto requestDto) {
        return itemClient.addComment(userId, itemId, requestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId, @RequestBody ItemRequestDto requestDto) {
        return itemClient.editItem(userId, itemId, requestDto);
    }
}
