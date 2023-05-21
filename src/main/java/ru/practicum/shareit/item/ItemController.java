package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody @Validated ItemDto item) {
        log.info("Add new item.");
        return itemService.addItem(userId, item);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable(value = "itemId") Long itemId,
                                 @RequestBody @Validated CommentDto commentDto) {
        log.info("Add new comment.");
        return itemService.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto item) {
        log.info("Update item with id {}", itemId);
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        log.info("Get item with id {}", itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get items by user with id {}", userId);
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Get items with text {}", text);
        return itemService.searchItems(text);
    }


}
