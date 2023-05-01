package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InMemoryItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private final UserStorage userStorage;
    private Long itemId = 1L;

    @Override
    public Item addItem(Long userId, ItemDto item) {
        User owner = userStorage.getUser(userId);
        Item newItem = ItemMapper.toItem(itemId++, owner, item);
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, ItemDto item) {
        Item updatedItem = getItem(itemId);

        if (!updatedItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("This user cannot edit this item");
        }

        if (item.getName() != null) {
            updatedItem = updatedItem.toBuilder()
                    .name(item.getName())
                    .build();
        }

        if (item.getDescription() != null) {
            updatedItem = updatedItem.toBuilder()
                    .description(item.getDescription())
                    .build();
        }

        if (item.getAvailable() != null) {
            updatedItem = updatedItem.toBuilder()
                    .available(item.getAvailable())
                    .build();
        }

        items.put(itemId, updatedItem);
        return items.get(updatedItem.getId());
    }

    @Override
    public Item getItem(Long itemId) {
        checkItem(itemId);
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByOwner(Long userId) {
        return items.values().stream()
                .filter(o -> Objects.equals(o.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream()
                .filter(o -> o.getAvailable().equals(true)
                && !text.isBlank()
                && o.getDescription()
                        .concat(o.getName()).toLowerCase()
                        .contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void checkItem(Long itemId) {
        if (items.get(itemId) == null) {
            throw new NotFoundException("Item with id " + itemId + " not found");
        }
    }
}
