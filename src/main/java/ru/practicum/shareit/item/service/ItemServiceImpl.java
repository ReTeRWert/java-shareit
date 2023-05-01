package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Override
    public Item addItem(Long userId, ItemDto item) {
        return itemStorage.addItem(userId, item);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, ItemDto item) {
        return itemStorage.updateItem(userId, itemId, item);
    }

    @Override
    public Item getItem(Long itemId) {
        return itemStorage.getItem(itemId);
    }

    @Override
    public List<Item> getItemsByOwner(Long userId) {
        return itemStorage.getItemsByOwner(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemStorage.searchItems(text);
    }
}
