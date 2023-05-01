package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @RequestBody @Validated ItemDto item) {
        return itemService.addItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId,
                           @RequestBody ItemDto item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<Item> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }


}
