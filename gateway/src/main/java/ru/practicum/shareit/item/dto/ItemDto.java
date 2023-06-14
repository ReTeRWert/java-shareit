package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "item name should be not blank or null")
    private String name;
    @NotBlank(message = "item description should be not blank or null")
    @Size(max = 200, message = "item description should be not longer than 200 letters")
    private String description;
    @NotNull(message = "item available should be not null")
    private Boolean available;
    private Long owner;
    private Long requestId;
}