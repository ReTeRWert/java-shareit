package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    @NotNull
    @NotBlank
    private final String name;
    @NotNull
    private final String description;
    @NotNull
    private final Boolean available;

}
