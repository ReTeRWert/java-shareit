package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    @Override
    public ItemDto addItem(Long userId, ItemDto item) {
        return ItemMapper.toItemDto(
                itemStorage.addItem(userId, item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        return ItemMapper.toItemDto(
                itemStorage.updateItem(userId, itemId, item));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemMapper.toItemDto(
                itemStorage.getItem(itemId));
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        return itemStorage.getItemsByOwner(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemStorage.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
