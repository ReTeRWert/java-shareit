package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

public class ItemMapper {

    public static Item toItem(User owner, ItemDto item) {
        return new Item(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                owner);
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }
}
