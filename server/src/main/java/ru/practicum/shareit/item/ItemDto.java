package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long requestId;
    private List<CommentDto> comments;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;

    public ItemDto(Long id, String name, String description, Boolean available, Long userId, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = userId;
        this.requestId = requestId;
    }
}
