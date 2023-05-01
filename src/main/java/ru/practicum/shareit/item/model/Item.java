package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class Item {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final User owner;
    private ItemRequest request;
}
