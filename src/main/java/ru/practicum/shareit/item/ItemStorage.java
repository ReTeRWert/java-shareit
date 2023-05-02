package ru.practicum.shareit.item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Long userId, ItemDto item);

    Item updateItem(Long userId, Long itemId, ItemDto item);

    Item getItem(Long itemId);

    List<Item> getItemsByOwner(Long userId);

    List<Item> searchItems(String text);
}
