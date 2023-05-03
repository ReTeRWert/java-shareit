package ru.practicum.shareit.item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item item);

    Item updateItem(Long itemId, Item item);

    Item getItem(Long itemId);

    List<Item> getItemsByOwner(Long userId);

    List<Item> searchItems(String text);
}
