package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Long userId, ItemDto item);

    Item updateItem(Long userId, Long itemId, ItemDto item);

    Item getItem(Long itemId);

    List<Item> getItemsByOwner(Long userId);

    List<Item> searchItems(String text);
}
