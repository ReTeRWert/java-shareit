package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.ItemInfoDto;
import ru.practicum.shareit.user.UserInfoDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private ItemInfoDto item;
    private UserInfoDto booker;
    private Status status;
}
