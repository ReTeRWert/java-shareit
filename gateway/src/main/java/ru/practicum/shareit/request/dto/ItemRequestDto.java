package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "request description could not be null or blank")
    private String description;
    private Long requestorId;
    private LocalDateTime created;
    private List<ItemDto> items;

    public ItemRequestDto() {
    }
}
