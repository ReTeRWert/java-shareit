package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.ItemInfoDto;
import ru.practicum.shareit.user.UserInfoDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingDto {

    private Long id;

    @NotNull
    @Future(message = "Start time should be in the future.")
    private LocalDateTime start;

    @NotNull
    @Future(message = "End time should be in the future.")
    private LocalDateTime end;

    @NotNull
    private Long itemId;
    private ItemInfoDto item;
    private UserInfoDto booker;
    private Status status;
}
