package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(value = "from", defaultValue = "0") Integer from, @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemService.getUserItems(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/search")
    public List<ItemDto> getNeedItems(String text, @RequestParam(value = "from", defaultValue = "0") Integer from, @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return itemService.getNeedItems(text, PageRequest.of(from / size, size));
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody ItemDto itemDto) {
        return itemService.addItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId, @RequestBody CommentDto commentDto) {
        return itemService.addComment(commentDto, userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId, @RequestBody ItemDto itemDto) {
        return itemService.editItem(itemDto, itemId, userId);
    }
}
