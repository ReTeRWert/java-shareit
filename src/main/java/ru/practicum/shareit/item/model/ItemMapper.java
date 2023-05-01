package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {

    public static Item toItem(Long itemId, User owner, ItemDto item) {
        return new Item(itemId,
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                owner);
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }
}
