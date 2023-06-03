package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> getItemsByOwner(Long userId, Long from, Integer size);

    List<ItemDto> searchItems(String text, Long from, Integer size);

    CommentDto addComment(Long userId, Long itemId, CommentDto comment);

    Item getItemIfExist(Long itemId);

    List<Item> getItemsByRequestIdIn(List<Long> requestIds);

    List<Item> getItemsByRequestIdIs(Long requestId);
}
