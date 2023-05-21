package ru.practicum.shareit.item;

public class ItemMapper {

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId()
        );
    }

    public static ItemInfoDto toItemInfoDto(Item item) {
        return new ItemInfoDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }
}
