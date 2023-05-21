package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long owner;
    private List<CommentDto> comments;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;

    public ItemDto(Long id, String name, String description, Boolean available, Long userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = userId;
    }
}
