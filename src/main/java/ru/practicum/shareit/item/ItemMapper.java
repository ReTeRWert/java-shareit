package ru.practicum.shareit.item;

public class ItemMapper {

    public static Item dtoToItem(ItemDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }

    public static ItemDto itemToDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemInfoDto itemToInfoDto(Item item) {
        return new ItemInfoDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }
}
